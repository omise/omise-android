package co.omise.android.extensions

import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.models.APIError
import co.omise.android.models.ChargeStatus
import co.omise.android.models.Token
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
                    currentToken = observeChargeStatus(this@observeToken, tokenID, delay)
                    currentToken?.let(listener::onRequestSucceed)
                } catch (e: Exception) {
                    listener.onRequestFailed(e)
                }
            }
            withTimeout(maxTimeout) {
                job.await()
            }
        } catch (e: TimeoutCancellationException) {
            currentToken?.let(listener::onRequestSucceed)
        }
    }
}

suspend fun observeChargeStatus(client: Client, tokenID: String, delay: Long): Token {
    try {
        val request = Token.GetTokenRequestBuilder(tokenID).build()
        val token = client.send(request)
        return when (token.chargeStatus) {
            ChargeStatus.Unknown,
            ChargeStatus.Pending -> {
                delay(delay)
                observeChargeStatus(client, tokenID, delay)
            }
            else -> {
                token
            }
        }
    } catch (e: APIError) {
        if (e.code == "search_unavailable") {
            delay(delay)
            return observeChargeStatus(client, tokenID, delay)
        } else {
            throw e
        }
    } catch (e: Throwable) {
        throw e
    }
}
