package co.omise.android.api

import okhttp3.CertificatePinner
import okhttp3.HttpUrl
import java.util.*


/**
 * Endpoints encapsulates information about a particular Omise API endpoint.
 * Currently there exists 2 endpoints, the API and the VAULT.
 *
 *
 * This class encapsulates the following information for each endpoint:
 *
 *  * Host and network scheme (defaults to HTTPS.)
 *  * The certificate hash to pin against.
 *
 */
abstract class Endpoint {

    /**
     * The scheme to use, defaults to HTTPS.
     *
     * @return A [String] containing the network scheme to use.
     */
    private fun scheme(): String {
        return "https"
    }

    /**
     * The host name to connect to.
     *
     * @return A [String] containing the host name to connect to.
     */
    abstract fun host(): String

    /**
     * The certificate hash to use with OkHttp's [CertificatePinner].
     * The default implementation returns a certificate hash for `*.omise.co` domains.
     *
     * @return A [String] containing the cert hash to pin against or `null` to
     * pin no certificate.
     */
    fun certificateHash(): String {
        return "sha256/maqNsxEnwszR+xCmoGUiV636PvSM5zvBIBuupBn9AB8="
    }

    /**
     * The authentication key to use. The key should be taken from the given [Config] object.
     * [Config.publicKey] should be returned.
     *
     * @param config A [Config] instance.
     * @return A [String] containing the authentication key.
     */
    internal abstract fun authenticationKey(config: Config): String

    fun buildUrl(): HttpUrl.Builder {
        return HttpUrl.Builder()
                .scheme(scheme())
                .host(host())
    }

    companion object {
        @JvmField
        val VAULT: Endpoint = object : Endpoint() {
            override fun host(): String {
                return "vault.omise.co"
            }

            override fun authenticationKey(config: Config): String {
                return config.publicKey()
            }
        }

        @JvmField
        val API: Endpoint = object : Endpoint() {
            override fun host(): String {
                return "api.omise.co"
            }

            override fun authenticationKey(config: Config): String {
                return config.publicKey()
            }
        }

        val allEndpoints: List<Endpoint>
            get() {
                val endpoints = ArrayList<Endpoint>()
                endpoints.add(VAULT)
                endpoints.add(API)
                return Collections.unmodifiableList(endpoints)
            }

        val allEndpointsByHost: Map<String, Endpoint>
            get() {
                val endpoints = HashMap<String, Endpoint>()
                endpoints[VAULT.host()] = VAULT
                endpoints[API.host()] = API
                return Collections.unmodifiableMap(endpoints)
            }

    }
}
