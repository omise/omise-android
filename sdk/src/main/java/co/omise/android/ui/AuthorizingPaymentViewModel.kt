package co.omise.android.ui

import NetceteraConfig
import android.app.Activity
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.OmiseException
import co.omise.android.ThreeDS2ServiceWrapper
import co.omise.android.api.Client
import co.omise.android.config.UiCustomization
import co.omise.android.models.Authentication
import co.omise.android.models.Serializer
import com.netcetera.threeds.sdk.ThreeDS2ServiceInstance
import com.netcetera.threeds.sdk.api.exceptions.InvalidInputException
import com.netcetera.threeds.sdk.api.transaction.Transaction
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

internal class AuthorizingPaymentViewModelFactory(
    private val activity: Activity,
    private val urlVerifier: AuthorizingPaymentURLVerifier,
    private val uiCustomization: UiCustomization,
    private val passedThreeDSRequestorAppURL: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val client = Client("")
        val wrapper =
            ThreeDS2ServiceWrapper(
                context = activity.application,
                threeDS2Service = ThreeDS2ServiceInstance.get(),
                uiCustomizationMap = uiCustomization.uiCustomizationMap,
            )
        return AuthorizingPaymentViewModel(client, urlVerifier, wrapper, passedThreeDSRequestorAppURL) as T
    }
}

internal class AuthorizingPaymentViewModel(
    private val client: Client,
    private val urlVerifier: AuthorizingPaymentURLVerifier,
    private val threeDS2Service: ThreeDS2ServiceWrapper,
    private val passedThreeDSRequestorAppURL: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel(), ChallengeStatusReceiver {
    /** The [Authentication.AuthenticationStatus] of the authentication request. */
    private val _authenticationStatus = MutableLiveData<Authentication.AuthenticationStatus>()
    val authenticationStatus: LiveData<Authentication.AuthenticationStatus> = _authenticationStatus

    /** The [TransactionStatus] of the challenge process. */
    private val _transactionStatus = MutableLiveData<TransactionStatus>()
    val transactionStatus: LiveData<TransactionStatus> = _transactionStatus

    private val authenticationResponse = MutableLiveData<Authentication>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<OmiseException>()
    val error: LiveData<OmiseException> = _error

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, e ->
            _isLoading.postValue(false)
            threeDS2Service.transaction.close()
            if (e is OmiseException) {
                _error.postValue(e)
            } else {
                _error.postValue(OmiseException(Authentication.AuthenticationStatus.FAILED.message!!, e))
            }
        }

    init {
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            if (!urlVerifier.verifyExternalURL()) {

                val netceteraConfig = getConfigs()

                if (netceteraConfig != null) {
                    threeDS2Service.initialize(netceteraConfig).fold(
                        onSuccess = {
                            sendAuthenticationRequest(netceteraConfig)
                        },
                        onFailure = {
                            _error.postValue(OmiseException(OmiseSDKError.THREE_DS2_INITIALIZATION_FAILED.value, it))
                        },
                    )
                }
            }
        }
    }

    // Use the authorize_uri to create the config url
    fun createNetceteraConfigUrl(authUrl: String): String {
        try {
            // Check if the authUrl is valid
            URL(authUrl)
            val base = Uri.parse(authUrl).buildUpon().clearQuery().build()
            val parts = base.toString().split('/').toMutableList()
            parts[parts.lastIndex] = "config"
            val configEndPoint = parts.joinToString("/")
            // check that the generated url is valid
            URL(configEndPoint)
            return configEndPoint
        } catch (e: Exception) {
            throw InvalidInputException("Invalid URL: $authUrl", e)
        }
    }

    private suspend fun getConfigs(): NetceteraConfig? {
        var netceteraConfig: NetceteraConfig? = null
        try {
            // create the config endpoint url
            val configUrl = createNetceteraConfigUrl(urlVerifier.authorizedURLString)
            // No body params so we just need to perform a GET on the config url
            val request = NetceteraConfig.NetceteraConfigRequestBuilder().configUrl(configUrl).build()
            // get the configuration from API
            netceteraConfig = client.send(request)
        } catch (e: Exception) {
            _error.postValue(OmiseException(OmiseSDKError.UNABLE_TO_GET_CONFIGS.value, e))
        }
        return netceteraConfig
    }

    private suspend fun sendAuthenticationRequest(netceteraConfig: NetceteraConfig) {
        val transaction = threeDS2Service.createTransaction(netceteraConfig.directoryServerId!!, netceteraConfig.messageVersion!!)
        val authenticationRequestParameters = transaction.authenticationRequestParameters
        val request =
            Authentication.AuthenticationRequestBuilder()
                .authorizeUrl(urlVerifier.authorizedURLString)
                .areq(
                    Authentication.AuthenticationRequestBuilder.AReq(
                        sdkAppID = authenticationRequestParameters.sdkAppID,
                        sdkEphemPubKey =
                            Serializer().objectMapper().readValue(
                                authenticationRequestParameters.sdkEphemeralPublicKey.byteInputStream(),
                                Authentication.AuthenticationRequestBuilder.SdkEphemPubKey::class.java,
                            ),
                        sdkTransID = authenticationRequestParameters.sdkTransactionID,
                        sdkMaxTimeout = 5,
                    ),
                )
                .encryptedDeviceInfo(
                    authenticationRequestParameters.deviceData,
                ).build()

        try {
            _isLoading.postValue(true)
            val authentication = client.send(request)

            // Recommended by Netcetera's 3DS SDK.
            // If the challenge flow is required
            // - Do not close the transaction
            // - Do not hide the progress view
            // See https://3dss.netcetera.com/3dssdk/doc/2.7.0/android-sdk-api#processing-screen
            if (authentication.status != Authentication.AuthenticationStatus.CHALLENGE) {
                _isLoading.postValue(false)
                transaction.close()
            }

            authenticationResponse.postValue(authentication)
            _authenticationStatus.postValue(authentication.status)
        } catch (e: Exception) {
            _isLoading.postValue(false)
            transaction.close()
            _error.postValue(OmiseException(Authentication.AuthenticationStatus.FAILED.message!!, e))
        }
    }

    internal fun createThreeDSRequestorAppURL(sdkTransID: String?): String {
        // Check if the URL already contains a query string
        val separator = if (passedThreeDSRequestorAppURL.contains("?")) "&" else "?"
        // Append the transaction ID to the URL
        return "$passedThreeDSRequestorAppURL${separator}transID=$sdkTransID"
    }

    fun doChallenge(activity: Activity) {
        val ares = authenticationResponse.value?.ares ?: return
        val challengeParameters =
            ChallengeParameters().apply {
                set3DSServerTransactionID(ares.threeDSServerTransID)
                threeDSRequestorAppURL = createThreeDSRequestorAppURL(ares.sdkTransID)
                acsTransactionID = ares.acsTransID
                acsRefNumber = ares.acsReferenceNumber
                acsSignedContent = ares.acsSignedContent
            }

        try {
            threeDS2Service.doChallenge(activity, challengeParameters, this, 5)
        } catch (e: Exception) {
            _error.postValue(OmiseException(ChallengeStatus.FAILED.value, e))
        }
    }

    fun getTransaction(): Transaction {
        return threeDS2Service.transaction
    }

    /** [ChallengeStatusReceiver] implementation. */
    override fun completed(event: com.netcetera.threeds.sdk.api.transaction.challenge.events.CompletionEvent?) {
        when (event?.transactionStatus) {
            "Y" -> _transactionStatus.postValue(TransactionStatus.AUTHENTICATED)
            "N" -> _transactionStatus.postValue(TransactionStatus.NOT_AUTHENTICATED)
            else ->
                _error.postValue(
                    OmiseException(
                        ChallengeStatus.COMPLETED_WITH_UNKNOWN_STATUS.includeUnknownTransactionStatusWithError(event?.transactionStatus),
                    ),
                )
        }
    }

    override fun cancelled() {
        _error.postValue(OmiseException(ChallengeStatus.CANCELLED.value))
    }

    override fun timedout() {
        _error.postValue(OmiseException(ChallengeStatus.TIMED_OUT.value))
    }

    override fun protocolError(event: com.netcetera.threeds.sdk.api.transaction.challenge.events.ProtocolErrorEvent?) {
        _error.postValue(OmiseException(ChallengeStatus.PROTOCOL_ERROR.value))
    }

    override fun runtimeError(event: com.netcetera.threeds.sdk.api.transaction.challenge.events.RuntimeErrorEvent?) {
        _error.postValue(OmiseException(ChallengeStatus.RUNTIME_ERROR.value))
    }
    /** End of [ChallengeStatusReceiver] implementation. */
}

enum class ChallengeStatus(val value: String) {
    RUNTIME_ERROR("Challenge runtime error"),
    PROTOCOL_ERROR("Challenge protocol error"),
    FAILED("Challenge failed"),
    TIMED_OUT("Challenge timed out"),
    CANCELLED("Challenge cancelled"),
    COMPLETED_WITH_UNKNOWN_STATUS("Challenge completed with unknown status"),
    ;

    // Custom constructor to include the transaction status in the error message when the transaction status is unknown
    fun includeUnknownTransactionStatusWithError(transactionStatus: String?): String {
        return "Challenge completed with unknown status: $transactionStatus"
    }
}

enum class OmiseSDKError(val value: String) {
    OPEN_DEEP_LINK_FAILED("Open deep link failed"),
    THREE_DS2_INITIALIZATION_FAILED("3DS2 initialization failed"),
    UNABLE_TO_GET_CONFIGS("Unable to get configs, can't initialize 3DS2 SDK"),
}
