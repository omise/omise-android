package co.omise.android.api

import android.os.Handler
import android.os.Looper
import co.omise.android.models.Model
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Client is the main entry point to the SDK and it needs to be supplied with a public API Key.
 * You can use the Client to send a [Request]. The constructor creates a Client that sends the
 * specified API version in the header to access the latest version of the Omise API.
 *
 * @param publicKey The API key with the `pkey_` prefix.
 *
 * @see Request
 * @see [Security Best Practices](https://www.omise.co/security-best-practices)
 */
class Client(publicKey: String) {
    private var httpClient: OkHttpClient
    private val background: Executor
    private val handler = Handler(Looper.getMainLooper())

    init {
        background = Executors.newSingleThreadExecutor()
        val config = Config(publicKey = publicKey)
        httpClient = buildHttpClient(config)
    }

    /**
     * Sends the supplied request and invokes the callback on the listener.
     *
     * @param request  The [Request] to be sent.
     * @param listener The [RequestListener] to listen for request response.
     */
    fun <T : Model> send(
        request: Request<T>,
        listener: RequestListener<T>,
    ) {
        background.execute { Invocation(handler, httpClient, request, listener).invoke() }
    }

    suspend fun <T : Model> send(request: Request<T>) =
        suspendCoroutine { continuation ->
            send(
                request,
                object : RequestListener<T> {
                    override fun onRequestSucceed(model: T) {
                        continuation.resume(model)
                    }

                    override fun onRequestFailed(throwable: Throwable) {
                        continuation.resumeWithException(throwable)
                    }
                },
            )
        }

    private fun buildHttpClient(config: Config): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val spec =
            ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .build()

        return builder
            .addInterceptor(Configurer(config))
            .connectionSpecs(listOf(spec))
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }
}
