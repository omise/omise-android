package co.omise.android.api

import co.omise.android.models.Model
import okhttp3.HttpUrl
import okhttp3.RequestBody
import java.util.Objects.requireNonNull


/**
 * Request Builder is a base class, any classes that extends from it would be
 * responsible for creating a particular [Request] and allows for the Request
 * method, path, payload and class type to be configured.
 *
 * @param <T> the generic type for any Model that would need to be returned by the [Client] when this request is passed to it
 */
abstract class RequestBuilder<T : Model> {

    /**
     * Builds request with all its enclosing information and payload (if available)
     *
     * @return built [Request] of type [Model]
     */
    fun build(): Request<T> {
        return Request(method(), path(), payload(), type())
    }

    /**
     * Default HTTP method.
     *
     * @return HTTP method as a string
     */
    open fun method(): String {
        return GET
    }

    /**
     * Abstract method that needs to be implement by all children of this class to provide API Path
     *
     * @return the url path as [HttpUrl]
     */
    protected abstract fun path(): HttpUrl

    /**
     * Additional parameters for the request, which is null by default for requests that do not accept params (eg: GET)
     *
     * @return the params as a [RequestBody]
     */
    open fun payload(): RequestBody? {
        //Has to be null as it would fail for GET requests
        return null
    }

    /**
     * Abstract method that needs to be implement by all children of this class to provide response type
     *
     * @return Class type of response
     */
    protected abstract fun type(): Class<T>


    /**
     * Builds and returns a valid [HttpUrl] pointing to the given [Endpoint]'s host
     * and with all the supplied segments concatenated.
     *
     * @param endpoint The Omise API [Endpoint] to point to.
     * @param path     The base API path.
     * @return An [HttpUrl] instance.
     */
    protected fun buildUrl(endpoint: Endpoint, path: String): HttpUrl {
        return HttpUrlBuilder(endpoint, path).build()
    }

    inner class HttpUrlBuilder(private val endpoint: Endpoint, private val path: String) {

        fun build(): HttpUrl {
            requireNonNull(endpoint)
            requireNonNull(path)
            val builder: HttpUrl.Builder = endpoint.buildUrl().addPathSegment(path)
            return builder.build()
        }
    }

    companion object {
        const val POST = "POST"
        const val GET = "GET"
        const val PATCH = "PATCH"
        const val DELETE = "DELETE"
    }
}
