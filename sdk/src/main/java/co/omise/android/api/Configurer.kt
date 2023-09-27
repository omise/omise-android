package co.omise.android.api

import co.omise.android.SDKLog
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


/**
 * Configurer is used in the [Client] class to handle HTTP request configuration.
 * The [Configurer.configure] function can also be used to directly configure
 * requests if a custom [okhttp3.OkHttpClient] is used, without the need to go
 * through the Omise [Client].
 *
 * @param config [Config] class that contains [Client] configuration information.
 */
class Configurer internal constructor(private val config: Config) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        SDKLog.d("${request.method} ${request.url.encodedPath}")

        val response = chain.proceed(configure(config, chain.request()))
        SDKLog.d("${request.method} ${request.url.encodedPath} - ${response.code}")

        return response
    }

    companion object {

        /**
         * Configures an HTTP [Request] according to the given [Config].
         *
         * @param config  A [Config] class containing the configuration information to be used.
         * @param request Original HTTP [Request] to configure.
         * @return A new HTTP [Request] instance with configurations from [config] applied.
         */
        @JvmStatic
        fun configure(config: Config, request: Request): Request {
            val apiVersion = config.apiVersion()
            val endpoint = Endpoint.allEndpointsByHost[request.url.host]
                    ?: throw UnsupportedOperationException("unknown endpoint: " + request.url.host)

            val key = endpoint.authenticationKey(config)
            var builder = request.newBuilder()
                    .addHeader("User-Agent", config.userAgent())
                    .addHeader("Authorization", Credentials.basic(key, "x"))

            if (apiVersion.isNotEmpty()) {
                builder = builder.addHeader("Omise-Version", apiVersion)
            }

            return builder.build()
        }
    }
}
