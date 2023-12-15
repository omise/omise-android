package co.omise.android.api

import okhttp3.HttpUrl
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap

/**
 * Endpoint is a base class that can be used to encapsulate information
 * about a particular Omise API endpoint. Currently there are two endpoint
 * classes: "API" and "VAULT".
 *
 * This class includes the following information for each endpoint:
 *
 *  * Host and network scheme (defaults to HTTPS).
 *  * The certificate hash to pin against.
 *
 */
abstract class Endpoint {
    /**
     * The scheme to use, defaults to HTTPS.
     *
     * @return A string containing the network scheme.
     */
    fun scheme(): String {
        return "https"
    }

    /**
     * The host name to connect to.
     *
     * @return A string containing the host name.
     */
    abstract fun host(): String

    /**
     * The authentication key to use. The key should be taken from the supplied [Config] class.
     * [Config.publicKey] should be returned.
     *
     * @param config A [Config] class instance.
     * @return A string containing the authentication key.
     */
    abstract fun authenticationKey(config: Config): String

    fun buildUrl(): HttpUrl.Builder {
        return HttpUrl.Builder()
            .scheme(scheme())
            .host(host())
    }

    companion object {
        /**
         * Class containing all the information the "VAULT" endpoint.
         */
        @JvmField
        val VAULT: Endpoint =
            object : Endpoint() {
                override fun host(): String = OMISE_VAULT

                override fun authenticationKey(config: Config): String {
                    return config.publicKey()
                }
            }

        /**
         * Class containing all the information the "API" endpoint.
         */
        @JvmField
        val API: Endpoint =
            object : Endpoint() {
                override fun host(): String = OMISE_API

                override fun authenticationKey(config: Config): String {
                    return config.publicKey()
                }
            }

        @JvmStatic
        val allEndpoints: List<Endpoint>
            get() {
                val endpoints = ArrayList<Endpoint>()
                endpoints.add(VAULT)
                endpoints.add(API)
                return Collections.unmodifiableList(endpoints)
            }

        @JvmStatic
        val allEndpointsByHost: Map<String, Endpoint>
            get() {
                val endpoints = HashMap<String, Endpoint>()
                endpoints[VAULT.host()] = VAULT
                endpoints[API.host()] = API
                return Collections.unmodifiableMap(endpoints)
            }
    }
}
