package co.omise.android.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.omise.android.api.Client
import co.omise.android.api.Request
import co.omise.android.api.RequestListener
import co.omise.android.models.APIError
import co.omise.android.models.ChargeStatus
import co.omise.android.models.Model
import co.omise.android.models.Token
import co.omise.android.threeds.core.SDKCoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


internal class AuthorizingPaymentViewModelFactory(private val omisePublicKey: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthorizingPaymentViewModel(Client(omisePublicKey)) as T
    }
}

internal class AuthorizingPaymentViewModel(private val client: Client) : ViewModel() {

    private val maxTimeout = 30_000L // 30 secs
    private val requestDelay = 3_000L // 3 secs
    private val scope = SDKCoroutineScope().coroutineScope

    private val _authorizingPaymentResult = MutableLiveData<Result<Token>>()
    val authorizingPaymentResult: LiveData<Result<Token>> = _authorizingPaymentResult

    fun pollingToken(tokenID: String) = scope.launch {
        try {
            val token = sendGetTokenRequest(tokenID)
            Log.d("polling token", token.chargeStatus.value)
            when (token.chargeStatus) {
                ChargeStatus.Successful -> _authorizingPaymentResult.postValue(Result.success(token))

                ChargeStatus.Reversed,
                ChargeStatus.Expired,
                ChargeStatus.Failed -> TODO()

                ChargeStatus.Unknown,
                ChargeStatus.Pending -> recurRetrieveToken(tokenID)
            }
        } catch (e: APIError) {
           e.printStackTrace()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private suspend fun sendGetTokenRequest(tokenID: String) =
            sendRequest(Token.GetTokenRequestBuilder(tokenID).build())

    private suspend fun <T : Model> sendRequest(request: Request<T>) = suspendCoroutine<T> { continuation ->
        client.send(request, object : RequestListener<T> {
            override fun onRequestSucceed(model: T) {
                continuation.resume(model)
            }

            override fun onRequestFailed(throwable: Throwable) {
                continuation.resumeWithException(throwable)
            }
        })
    }

    private suspend fun recurRetrieveToken(tokenID: String) {
        delay(requestDelay)

        pollingToken(tokenID)
    }

    fun cleanup() {
        scope.cancel()
    }
}
