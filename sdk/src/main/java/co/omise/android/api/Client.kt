package co.omise.android.api

import android.os.Build
import android.os.Handler
import co.omise.android.api.exceptions.ClientException
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
class Client

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
 * @param secretKey The key with `skey_` prefix.
 *
 * @see [Security Best Practices](https://www.omise.co/security-best-practices)
 *
 * @see Versioning(https://www.omise.co/api-versioning)
 */ private constructor(publicKey: String?, secretKey: String?) {

    private var httpClient: OkHttpClient
    private val background: Executor

    init {
        this.background = Executors.newSingleThreadExecutor()
    }

    init {
        if (publicKey == null && secretKey == null) {
            throw ClientException(IllegalArgumentException("The key must have at least one key."))
        }

        val config = Config(Endpoint.API_VERSION, publicKey, secretKey)
        httpClient = buildHttpClient(config)
        this.httpClient = buildHttpClient(config)
    }

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

    /**
     * Builds and returns a [Client]
     *
     * Note: Please ensure to have at least one of the keys supplied to have the client function correctly.
     *
     *
     * @see [Security Best Practices](https://www.omise.co/security-best-practices)
     *
     * @see [Versioning](https://www.omise.co/api-versioning)
     */
    class Builder {
        private var publicKey: String? = null
        private var secretKey: String? = null

        /**
         * Set public key.
         *
         * @param publicKey The key with the `pkey_` prefix.
         */
        fun publicKey(publicKey: String?): Builder {
            this.publicKey = publicKey
            return this
        }

        /**
         * Set secret key.
         *
         * @param secretKey The key with the `skey_` prefix.
         */
        fun secretKey(secretKey: String?): Builder {
            this.secretKey = secretKey
            return this
        }

        /**
         * Creates a new [Client] instance.
         *
         * @return the [Client]
         */
        fun build(): Client {
            return Client(publicKey, secretKey)
        }
    }
}
