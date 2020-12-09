package co.omise.android.ui

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.omise.android.api.Client
import co.omise.android.config.AuthorizingPaymentConfig
import co.omise.android.models.APIError
import co.omise.android.models.ChargeStatus
import co.omise.android.models.Token
import co.omise.android.threeds.ThreeDS
import co.omise.android.threeds.ThreeDSListener
import co.omise.android.threeds.core.ThreeDSConfig
import co.omise.android.threeds.data.models.TransactionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.jetbrains.annotations.TestOnly


internal class AuthorizingPaymentViewModelFactory(
        private val activity: Activity,
        private val omisePublicKey: String,
        private val tokenID: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        ThreeDSConfig.initialize(AuthorizingPaymentConfig.get().threeDSConfig.threeDSConfig)
        val threeDS = ThreeDS(activity)
        return AuthorizingPaymentViewModel(Client(omisePublicKey), threeDS, tokenID) as T
    }
}

internal class AuthorizingPaymentViewModel(
        private val client: Client,
        private val threeDS: ThreeDS,
        private val tokenID: String
) : ViewModel(), ThreeDSListener {

    private val maxTimeout = 30_000L // 30 secs
    private val requestDelay = 3_000L // 3 secs

    private val _authentication = MutableLiveData<AuthenticationResult>()
    val authentication: LiveData<AuthenticationResult> = _authentication

    private var currentToken: Token? = null

    init {
        threeDS.setThreeDSListener(this)
    }

    @TestOnly
    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    private var coroutineScope: CoroutineScope = viewModelScope

    fun observeChargeStatus() {
        coroutineScope.launch {
            try {
                val job = async { observeChargeStatus(tokenID) }
                withTimeout(maxTimeout) {
                    job.await()
                }
            } catch (e: TimeoutCancellationException) {
                currentToken?.let {
                    _authentication.postValue(AuthenticationResult.AuthenticationCompleted(it))
                }
            }
        }
    }

    private suspend fun observeChargeStatus(tokenID: String) {
        try {
            val token = sendGetTokenRequest(tokenID)
            currentToken = token
            when (token.chargeStatus) {
                ChargeStatus.Successful,
                ChargeStatus.Reversed,
                ChargeStatus.Expired,
                ChargeStatus.Failed -> {
                    _authentication.postValue(AuthenticationResult.AuthenticationCompleted(token))
                }

                ChargeStatus.Unknown,
                ChargeStatus.Pending -> {
                    delay(requestDelay)
                    observeChargeStatus(tokenID)
                }
            }
        } catch (e: APIError) {
            if (e.code == "search_unavailable") {
                delay(requestDelay)
                observeChargeStatus(tokenID)
            } else {
                _authentication.postValue(AuthenticationResult.AuthenticationFailure(e))
            }
        } catch (e: Throwable) {
            _authentication.postValue(AuthenticationResult.AuthenticationFailure(e))
        }
    }

    private suspend fun sendGetTokenRequest(tokenID: String): Token =
            client.send(Token.GetTokenRequestBuilder(tokenID).build())

    fun cleanup() {
        viewModelScope.cancel()
        threeDS.cleanup()
    }

    fun authorizeTransaction(authorizedUrl: String) {
        threeDS.authorizeTransaction(authorizedUrl)
    }

    override fun onCompleted(transStatus: TransactionStatus) {
        observeChargeStatus()
    }

    override fun onFailure(e: Throwable) {
        _authentication.postValue(AuthenticationResult.AuthenticationFailure(e))
    }

    override fun onUnsupported() {
        _authentication.postValue(AuthenticationResult.AuthenticationUnsupported)
    }
}

sealed class AuthenticationResult {
    object AuthenticationUnsupported : AuthenticationResult()
    data class AuthenticationCompleted(val token: Token) : AuthenticationResult()
    data class AuthenticationFailure(val error: Throwable) : AuthenticationResult()
}
