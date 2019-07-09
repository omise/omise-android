package co.omise.android.api

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


/**
 * Configurer handles HTTP requests configuration. You can use the [.configure] method
 * to setup your own [okhttp3.OkHttpClient] and avoid using the [Client] directly, for example.
 */
class Configurer internal constructor(private val config: Config) : Interceptor {

    init {
        requireNotNull(config)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(configure(config, chain.request()))
    }

    companion object {

        /**
         * Configures a [Request] according to the given [Config].
         *
         * @param config  A [Config] to use for configuration.
         * @param request An HTTP [Request] to configure.
         * @return A new [Request] instance with configurations from [Config] applied.
         */
        internal fun configure(config: Config, request: Request): Request? {
            val apiVersion = config.apiVersion()
            val endpoint = Endpoint.allEndpointsByHost[request.url().host()]
                    ?: throw UnsupportedOperationException("unknown endpoint: " + request.url().host())

            val key = endpoint.authenticationKey(config)
            var builder = request.newBuilder()
                    .addHeader("User-Agent", config.userAgent())
                    .addHeader("Authorization", Credentials.basic(key, "x"))

            if (apiVersion != null && !apiVersion.isEmpty()) {
                builder = builder.addHeader("Omise-Version", apiVersion)
            }

            return builder.build()
        }
    }
}