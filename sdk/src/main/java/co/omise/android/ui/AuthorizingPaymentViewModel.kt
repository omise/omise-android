package co.omise.android.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.omise.android.api.Client
import co.omise.android.models.APIError
import co.omise.android.models.ChargeStatus
import co.omise.android.models.Token
import co.omise.android.utils.SDKCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout


internal open class AuthorizingPaymentViewModelFactory(private val omisePublicKey: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthorizingPaymentViewModel(Client(omisePublicKey)) as T
    }
}

internal open class AuthorizingPaymentViewModel(
        private val client: Client,
        private val scope: CoroutineScope = SDKCoroutineScope().coroutineScope
) : ViewModel() {

    private val maxTimeout = 30_000L // 30 secs
    private val requestDelay = 3_000L // 3 secs

    private val _authorizingPaymentResult = MutableLiveData<Result<Token>>()
    open val authorizingPaymentResult: LiveData<Result<Token>> = _authorizingPaymentResult

    open fun observeTokenChange(tokenID: String) {
        scope.launch {
            try {
                val job = async { observeChargeStatus(tokenID) }
                withTimeout(maxTimeout) {
                    job.await()
                }
            } catch (e: TimeoutCancellationException) {
                _authorizingPaymentResult.postValue(Result.failure(e))
            }
        }
    }

    private suspend fun observeChargeStatus(tokenID: String) {
        try {
            val token = sendGetTokenRequest(tokenID)
            when (token.chargeStatus) {
                ChargeStatus.Successful,
                ChargeStatus.Reversed,
                ChargeStatus.Expired,
                ChargeStatus.Failed -> _authorizingPaymentResult.postValue(Result.success(token))

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
                _authorizingPaymentResult.postValue(Result.failure(e))
            }
        } catch (e: Throwable) {
            _authorizingPaymentResult.postValue(Result.failure(e))
        }
    }

    private suspend fun sendGetTokenRequest(tokenID: String) =
            client.send(Token.GetTokenRequestBuilder(tokenID).build())

    open fun cleanup() {
        scope.cancel()
    }
}
