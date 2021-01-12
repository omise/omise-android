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


/**
 * This is an utility function for observing the token until the charge status changed.
 *
 * @param id Token ID.
 * @param listener [RequestListener] the callback to be invoked when charnge staus changed or request failed.
 * @param interval time interval in millisecond.
 * @param timeout maximum timeout in millisecond.
 */
fun Client.observeTokenUntilChargeStatusChanged(id: String, listener: RequestListener<Token>, interval: Long = 3_000L, timeout: Long = 30_000L) {
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
                currentToken = retrieveToken(this@observeTokenUntilChargeStatusChanged, id)
            } while (currentToken == null || currentToken?.chargeStatus in listOf(ChargeStatus.Unknown, ChargeStatus.Pending))
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
