package co.omise.android.api

import android.os.Build
import co.omise.android.BuildConfig

/**
 * Config class bundles configuration values supplied to the [Client] constructor.
 *
 * @param apiVersion The API version to use.
 * @param publicKey  The key with the `pkey_` prefix.
 * @param secretKey  The key with the `skey_` prefix.
 *
 * @see Client
 */
class Config(
        private val apiVersion: String?,
        private val publicKey: String?,
        private val secretKey: String?
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
     * Returns the API version configuration. This value will be added as
     * a `Omise-Version` HTTP header during API calls.
     *
     * @return A [String] containing the configured API version.
     */
    fun apiVersion(): String? {
        return apiVersion
    }

    /**
     * Returns the configured public key. Public keys always have the `pkey_` prefix.
     *
     * @return A [String] containing the public key.
     */
    fun publicKey(): String? {
        return publicKey
    }

    /**
     * Returns the configured secret key. Secret keys always have the `skey_` prefix.
     *
     * @return A [String] containing the secret key.
     */
    fun secretKey(): String? {
        return secretKey
    }

    /**
     * Returns a valid user agent string for use with HTTP clients.
     *
     * @return A [String] containing the user agent.
     */
    fun userAgent(): String {
        return userAgent
    }
}
