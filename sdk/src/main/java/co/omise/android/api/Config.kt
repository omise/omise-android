package co.omise.android.api

import android.os.Build
import co.omise.android.BuildConfig

/**
 * Config class bundles configuration values used in the [Client] constructor
 * and are used in creating the HTTP requests.
 *
 * @param apiVersion The API version to use.
 * @param publicKey  The API key with the `pkey_` prefix.
 *
 * @see Client
 */
class Config(
    private val apiVersion: String = API_VERSION,
    private val publicKey: String,
) {
    private val userAgent: String

    init {
        this.userAgent = buildUserAgent()
    }

    private fun buildUserAgent(): String {
        return "OmiseAndroid/" + BuildConfig.VERSION_NAME +
            " Android/" + Build.VERSION.SDK_INT +
            " Model/" + Build.MODEL
    }

    /**
     * Returns the configured API version. This value will be added as `Omise-Version`
     * to the HTTP header during API calls.
     *
     * @return A string containing the configured API version.
     */
    fun apiVersion(): String {
        return apiVersion
    }

    /**
     * Returns the configured public API key. Public API keys always have the `pkey_` prefix.
     *
     * @return A string containing the public API key.
     */
    fun publicKey(): String {
        return publicKey
    }

    /**
     * Returns a valid user agent string for use with HTTP clients.
     *
     * @return A string containing the user agent.
     */
    fun userAgent(): String {
        return userAgent
    }

    companion object {
        internal const val API_VERSION = "2019-05-29"
    }
}
