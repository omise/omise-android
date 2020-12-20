package co.omise.android.extensions

import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.models.APIError
import co.omise.android.models.ChargeStatus
import co.omise.android.models.Token
import co.omise.android.ui.AuthenticationResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout


fun Client.observeToken(tokenID: String, listener: RequestListener<Token>) {
    var currentToken: Token? = null
    val delay = 3_000L
    val maxTimeout = 30_000L
    GlobalScope.launch {
        try {
            val job = async {
                try {
                    val request = Token.GetTokenRequestBuilder(tokenID).build()
                    val token = this@observeToken.send(request)
                    currentToken = token
                    when (token.chargeStatus) {
                        ChargeStatus.Successful,
                        ChargeStatus.Reversed,
                        ChargeStatus.Expired,
                        ChargeStatus.Failed -> {
//                    _authentication.postValue(AuthenticationResult.AuthenticationCompleted(token))
                        }

                        ChargeStatus.Unknown,
                        ChargeStatus.Pending -> {
                            delay(delay)
//                            observeChargeStatus(tokenID)
                        }
                    }
                } catch (e: APIError) {
                    if (e.code == "search_unavailable") {
                        delay(delay)
//                        observeChargeStatus(tokenID)
                    } else {
//                _authentication.postValue(AuthenticationResult.AuthenticationFailure(e))
                    }
                } catch (e: Throwable) {
//            _authentication.postValue(AuthenticationResult.AuthenticationFailure(e))
                }

            }
            withTimeout(maxTimeout) {
                job.await()
            }
        } catch (e: TimeoutCancellationException) {
            currentToken?.let {
//                _authentication.postValue(AuthenticationResult.AuthenticationCompleted(it))
            }
        }

    }

    suspend fun observeChargeStatus(tokenID: String) {
        try {
            val request = Token.GetTokenRequestBuilder(tokenID).build()
            val token = this@observeToken.send(request)
            currentToken = token
            when (token.chargeStatus) {
                ChargeStatus.Successful,
                ChargeStatus.Reversed,
                ChargeStatus.Expired,
                ChargeStatus.Failed -> {
//                    _authentication.postValue(AuthenticationResult.AuthenticationCompleted(token))
                }

                ChargeStatus.Unknown,
                ChargeStatus.Pending -> {
                    delay(delay)
                    observeChargeStatus(tokenID)
                }
            }
        } catch (e: APIError) {
            if (e.code == "search_unavailable") {
                delay(delay)
                observeChargeStatus(tokenID)
            } else {
//                _authentication.postValue(AuthenticationResult.AuthenticationFailure(e))
            }
        } catch (e: Throwable) {
//            _authentication.postValue(AuthenticationResult.AuthenticationFailure(e))
        }
    }

}
