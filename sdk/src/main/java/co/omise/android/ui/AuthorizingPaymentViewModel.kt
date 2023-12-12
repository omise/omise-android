package co.omise.android.ui

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.BuildConfig
import co.omise.android.OmiseException
import co.omise.android.ThreeDS2ServiceWrapper
import co.omise.android.api.Client
import co.omise.android.config.UiCustomization
import co.omise.android.models.Authentication
import co.omise.android.models.Serializer
import com.netcetera.threeds.sdk.ThreeDS2ServiceInstance
import com.netcetera.threeds.sdk.api.transaction.Transaction
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


internal class AuthorizingPaymentViewModelFactory(
    private val activity: Activity,
    private val urlVerifier: AuthorizingPaymentURLVerifier,
    private val uiCustomization: UiCustomization,
    private  val passedThreeDSRequestorAppURL: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val client = Client("")
        val wrapper = ThreeDS2ServiceWrapper(
            context = activity.application,
            threeDS2Service = ThreeDS2ServiceInstance.get(),
            uiCustomizationMap = uiCustomization.uiCustomizationMap,
        )
        return AuthorizingPaymentViewModel(client, urlVerifier, wrapper,passedThreeDSRequestorAppURL) as T
    }
}

internal class AuthorizingPaymentViewModel(
    private val client: Client,
    private val urlVerifier: AuthorizingPaymentURLVerifier,
    private val threeDS2Service: ThreeDS2ServiceWrapper,
    private  val passedThreeDSRequestorAppURL: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel(), ChallengeStatusReceiver {

    /** The [Authentication.AuthenticationStatus] of the authentication request. */
    private val _authenticationStatus = MutableLiveData<Authentication.AuthenticationStatus>()
    val authenticationStatus: LiveData<Authentication.AuthenticationStatus> = _authenticationStatus

    /** The [TransactionStatus] of the challenge process. */
    private val _transactionStatus = MutableLiveData<TransactionStatus>()
    val transactionStatus: LiveData<TransactionStatus> = _transactionStatus

    private val _authenticationResponse = MutableLiveData<Authentication>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<OmiseException>()
    val error: LiveData<OmiseException> = _error

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
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
                threeDS2Service.initialize().fold(
                    onSuccess = {
                        sendAuthenticationRequest()
                    },
                    onFailure = {
                        _error.postValue(OmiseException(OmiseSDKError.THREE_DS2_INITIALIZATION_FAILED.value, it))
                    }
                )
            }
        }
    }

    private suspend fun sendAuthenticationRequest() {
        // TODO: Replace this with real data
        val directoryServerId = BuildConfig.DS_ID
        val messageVersion = BuildConfig.MESSAGE_VERSION
        val transaction = threeDS2Service.createTransaction(directoryServerId, messageVersion)
        val authenticationRequestParameters = transaction.authenticationRequestParameters
        val request = Authentication.AuthenticationRequestBuilder()
            .authorizeUrl(urlVerifier.authorizedURLString)
            .areq(
                Authentication.AuthenticationRequestBuilder.AReq(
                    sdkAppID = authenticationRequestParameters.sdkAppID,
                    sdkEphemPubKey = Serializer().objectMapper().readValue(
                        authenticationRequestParameters.sdkEphemeralPublicKey.byteInputStream(),
                        Authentication.AuthenticationRequestBuilder.SdkEphemPubKey::class.java
                    ),
                    sdkTransID = authenticationRequestParameters.sdkTransactionID,
                    sdkMaxTimeout = 5,
                )
            )
            .deviceInfo(
                // TODO: Use deviceInfo from Netcetera's 3DS SDK after changing the authorize endpoint.
                // authenticationRequestParameters.deviceData
                Serializer().objectMapper().readValue(
                    """
                    {"DV":"1.6","DD":{"A006":"http:\/\/gsm.lge.com\/html\/gsm\/Nexus5-M3.xml","A007":"GoldfishNexus","A008":"us","A009":"310260","A010":"T-Mobile","A011":"10","A012":"1","A013":"1","A014":"us","A015":"310260","A016":"T-Mobile","A018":"5","A019":"Voicemail","A020":"+15557654321","A021":"true","A022":"false","A023":"false","A024":"true","A025":"true","A026":"true","A027":"false","A033":"false","A039":"02:00:00:00:00:00","A040":[],"A041":"true","A042":"goldfish_arm64","A043":"unknown","A044":"google","A045":"emu64a","A046":"TE1A.220922.012","A047":"google\/sdk_gphone64_arm64\/emu64a:13\/TE1A.220922.012\/9302419:user\/release-keys","A048":"ranchu","A049":"TE1A.220922.012","A050":"Google","A051":"sdk_gphone64_arm64","A052":"1.0.0.0","A054":[],"A055":["arm64-v8a"],"A056":"release-keys","A057":"1668654818000","A058":"user","A059":"android-build","A060":"REL","A061":"9302419","A062":"0","A063":"33","A064":"2022-11-05","A065":"false","A066":"false","A067":"true","A068":"http:\/\/www.google.com http:\/\/www.google.co.uk","A069":"76cc5517ef4e17fd","A070":"true","A071":"com.google.android.inputmethod.latin\/com.android.inputmethod.latin.LatinIME","A072":"true","A074":["com.google.android.inputmethod.latin","com.android.inputmethod.latin.LatinIME:com.google.android.tts","com.google.android.apps.speech.tts.googletts.settings.asr.voiceime.VoiceInputMethodService"],"A077":"true","A078":"false","A084":"true","A085":"cell,bluetooth,wifi,nfc,wimax","A086":"false","A087":"1","A088":"true","A089":"true","A090":"true","A093":"1","A094":"1","A095":"true","A097":"false","A099":"true","A103":"false","A104":"true","A105":"2","A106":"1","A107":"true","A108":"422","A109":"content:\/\/media\/internal\/audio\/media\/161?title=Pixie%20Dust&canonical=1","A110":"111","A111":"content:\/\/media\/internal\/audio\/media\/50?title=Flutey%20Phone&canonical=1","A112":"102","A113":"false","A114":"2147483647","A115":"true","A116":"false","A117":"false","A118":"false","A119":"false","A121":"0","A122":"false","A123":"false","A124":"false","A125":["android.ext.services.ExtServicesApplication","com.fime.emvco3ds.sdk.FimeApp","com.android.providers.media.MediaApplication","com.google.android.finsky.application.classic.ClassicApplication","com.android.nfc.NfcApplication","com.android.permissioncontroller.PermissionControllerApplication","com.google.android.setupwizard.SetupWizardApplication","com.android.se.SEApplication","com.google.android.apps.wellbeing.Wellbeing_Application","com.google.android.apps.docs.drive.DriveApplication","org.chromium.android_webview.nonembedded.WebViewApkApplication","org.chromium.chrome.browser.base.SplitChromeApplication","com.android.packageinstaller.PackageInstallerApplication","co.g.App","com.google.android.apps.speech.tts.googletts.GoogleTTSRoot_Application","com.android.managedprovisioning.ManagedProvisioningApplication","com.google.android.gms.common.app.GmsApplication","com.android.settings.SettingsApplication","com.android.phone.PhoneApp","com.android.systemui.SystemUIApplication","com.android.bluetooth.btservice.AdapterApp"],"A127":"88","A128":"19","A129":"mounted","A130":"789","A131":"2.75","A132":"440","A133":"2.75","A134":"72","A135":"72","A136":"6228115456","A137":"Mozilla\/5.0 (Linux; Android 13; sdk_gphone64_arm64 Build\/TE1A.220922.012; wv) AppleWebKit\/537.36 (KHTML, like Gecko) Version\/4.0 Chrome\/103.0.5060.71 Mobile Safari\/537.36","A138":"1","A139":"T-Mobile - US","A141":"1","A142":"T-Mobile - US","A143":"1","A145":"1","A146":"false","A149":[],"A150":"false","A151":"false","A152":"false","C001":"Android","C002":"Google||sdk_gphone64_arm64","C003":"Android TIRAMISU 13 API 33","C004":"13","C005":"en-US","C006":"420","C008":"1080x2214","C009":"sdk_gphone64_arm64","C010":"10.0.2.16","C013":"com.fime.emvco3ds.sdk.referenceapp","C014":"b8d20388-d177-4bf8-9d3b-ccf1f9cc56c0","C015":"1.0.0-alpha12","C016":"3DS_LOA_SDK_PPFU_020100_00007","C017":"20230830082957","C018":"4ae4af56-be8c-44fb-be06-4a99582307b7"},"DPNA":{"A001":"RE03","A002":"RE03","A003":"RE04","A004":"RE04","A005":"RE03","A017":"RE03","A028":"RE03","A029":"RE03","A030":"RE03","A031":"RE03","A032":"RE03","A034":"RE03","A035":"RE03","A036":"RE03","A037":"RE02","A038":"RE03","A053":"RE04","A073":"RE04","A075":"RE04","A076":"RE03","A079":"RE02","A080":"RE04","A081":"RE04","A082":"RE04","A083":"RE04","A091":"RE04","A092":"RE04","A096":"RE04","A098":"RE02","A100":"RE04","A101":"RE04","A102":"RE04","A120":"RE04","A126":"RE04","A140":"RE03","A147":"RE03","A148":"RE03","A153":"RE02","A154":"RE02","A155":"RE02","C011":"RE03","C012":"RE03"},"SW":["SW04"]}
                    """.trimIndent().byteInputStream(), Map::class.java
                ) as Map<String, Any>
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

            _authenticationResponse.postValue(authentication)
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
        return "$passedThreeDSRequestorAppURL${separator}transID=${sdkTransID}"
    }

    fun doChallenge(activity: Activity) {
        val ares = _authenticationResponse.value?.ares ?: return
        val challengeParameters = ChallengeParameters().apply {
            set3DSServerTransactionID(ares.threeDSServerTransID)
            setThreeDSRequestorAppURL(createThreeDSRequestorAppURL(ares.sdkTransID))
            acsTransactionID = ares.acsTransID
            // TODO : check if where to get the sdkReferenceNumber value
            acsRefNumber = BuildConfig.ACS_REF_NUMBER
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
            else -> _error.postValue(OmiseException(ChallengeStatus.COMPLETED_WITH_UNKNOWN_STATUS.includeUnknownTransactionStatusWithError(event?.transactionStatus)))
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
    COMPLETED_WITH_UNKNOWN_STATUS("Challenge completed with unknown status");


    // Custom constructor to include the transaction status in the error message when the transaction status is unknown
    fun includeUnknownTransactionStatusWithError(transactionStatus: String?): String {
        return "Challenge completed with unknown status: $transactionStatus"
    }
}

enum class OmiseSDKError(val value: String) {
    OPEN_DEEP_LINK_FAILED("Open deep link failed"),
    THREE_DS2_INITIALIZATION_FAILED("3DS2 initialization failed"),
}

