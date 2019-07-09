package co.omise.android.api

import android.os.Build
import android.os.Handler
import co.omise.android.models.Model
import okhttp3.CertificatePinner
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * Client is the main entry point to the SDK and it needs to be supplied with a public Key. You can use the Client to send a [Request].
 *
 * @param publicKey The key with the `pkey_` prefix.
 * @see Request
 */
class Client(publicKey: String)

/**
 * Creates a Client that sends the specified API version string in the header to access the latest version
 * of the Omise API.
 *
 *
 *
 * Note: Please ensure to have at least one of the keys supplied to have the client function correctly.
 *
 *
 * @param publicKey The key with `pkey_` prefix.
 *
 * @see [Security Best Practices](https://www.omise.co/security-best-practices)
 *
 */
{

    private var httpClient: OkHttpClient
    private val background: Executor

    /**
     * Sends the given request and invoke the callback on the listener.
     *
     * @param request  The [Request] to send.
     * @param listener The [RequestListener] to listen for request result.
     */
    fun <T : Model> send(request: Request<T>, listener: RequestListener<T>) {
        val handler = Handler()
        background.execute { Invocation(handler, httpClient, request, listener).invoke() }
    }

    private fun buildHttpClient(config: Config): OkHttpClient {
        val pinner = CertificatePinner.Builder()
        for (endpoint in Endpoint.allEndpoints) {
            pinner.add(endpoint.host(), endpoint.certificateHash())
        }

        val builder = OkHttpClient.Builder()
        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .build()

        if (Build.VERSION.SDK_INT < 21) {
            val trustManager = TLSPatch.systemDefaultTrustManager()
            builder.sslSocketFactory(TLSPatch.TLSSocketFactory(), trustManager)
        }

        return builder
                .addInterceptor(Configurer(config))
                .connectionSpecs(listOf(spec))
                .certificatePinner(pinner.build())
                .readTimeout(60, TimeUnit.SECONDS)
                .build()
    }

    init {
        background = Executors.newSingleThreadExecutor()
        val config = Config(publicKey = publicKey)
        httpClient = buildHttpClient(config)
    }
}
