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


fun Client.observeToken(id: String, interval: Long = 3_000L, timeout: Long = 30_000L, listener: RequestListener<Token>) {
    GlobalScope.launch {
        var currentToken: Token? = null
        var isFirstRequest = true
        val job = async {
            do {
                if (isFirstRequest) {
                    isFirstRequest = false
                } else {
                    delay(interval)
                }
                currentToken = retrieveToken(this@observeToken, id)
            } while (!isChargeStatusUpdated(currentToken))
            currentToken?.let(listener::onRequestSucceed)
        }

        try {
            withTimeout(timeout) {
                job.await()
            }
        } catch (e: TimeoutCancellationException) {
            currentToken?.let(listener::onRequestSucceed)
        }
    }
}

private fun isChargeStatusUpdated(token: Token?): Boolean {
    return token != null && token.chargeStatus !in listOf(ChargeStatus.Unknown, ChargeStatus.Pending)
}

private suspend fun retrieveToken(client: Client, tokenID: String): Token? {
    try {
        val request = Token.GetTokenRequestBuilder(tokenID).build()
        return client.send(request)
    } catch (e: APIError) {
        if (e.code == "search_unavailable") {
            return null
        } else {
            throw e
        }
    } catch (e: Throwable) {
        throw e
    }
}
