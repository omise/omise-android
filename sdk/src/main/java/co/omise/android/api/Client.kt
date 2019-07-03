package co.omise.android.api

import android.os.Build
import android.os.Handler
import co.omise.android.BuildConfig
import co.omise.android.models.Model
import okhttp3.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Client is the main entrypoint to the SDK and it needs to be supplied with a public Key. You can use the Client to send a [Request].
 *
 * @param publicKey The key with the `pkey_` prefix.
 * @see Request
 */
class Client(private val publicKey: String) {

    private val httpClient: OkHttpClient
    private val background: Executor

    init {
        this.httpClient = buildHttpClient()
        this.background = Executors.newSingleThreadExecutor()
    }

    /**
     * Sends the given request and invoke the callback on the listener.
     *
     * @param request  The request to send.
     * @param listener The listener to listen for request result.
     */
    fun <T : Model> send(request: Request<T>, listener: RequestListener<T>) {
        val handler = Handler()
        background.execute { Invocation(handler, httpClient, request, listener).invoke() }
    }

    private fun buildHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .build()

        if (Build.VERSION.SDK_INT < 21) {
            val trustManager = TLSPatch.systemDefaultTrustManager()
            builder.sslSocketFactory(TLSPatch.TLSSocketFactory(), trustManager)
        }

        return builder
                .addInterceptor(buildInterceptor())
                .connectionSpecs(listOf(spec))
                .certificatePinner(buildCertificatePinner())
                .readTimeout(60, TimeUnit.SECONDS)
                .build()
    }

    private fun buildCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
                .add("vault.omise.co", "sha256/maqNsxEnwszR+xCmoGUiV636PvSM5zvBIBuupBn9AB8=")
                .build()
    }

    private fun buildInterceptor(): Interceptor {
        return Interceptor { chain ->
            chain.proceed(chain.request()
                    .newBuilder()
                    .addHeader("User-Agent", buildUserAgent())
                    .addHeader("Authorization", Credentials.basic(publicKey, "x"))
                    .build())
        }
    }

    private fun buildUserAgent(): String {
        return "OmiseAndroid/" + BuildConfig.VERSION_NAME +
                " Android/" + Build.VERSION.SDK_INT +
                " Model/" + Build.MODEL
    }
}
