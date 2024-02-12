package co.omise.android.api

import co.omise.android.models.APIError
import co.omise.android.models.Model
import co.omise.android.models.Serializer
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Objects.requireNonNull
import kotlin.jvm.Throws

/**
 * Request Builder is a base class, any classes that extends from it would be
 * responsible for creating a particular [Request] and allows for the Request
 * method, path, payload and class type to be configured.
 *
 * @param <T> the generic type for any Model that would need to be returned by
 * the [Client] when this request is passed to it.
 */
abstract class RequestBuilder<T : Model> {
    /**
     * Builds request with all its enclosing information and payload (if available).
     *
     * @return built [Request] of type [Model].
     */
    fun build(): Request<T> {
        return Request(method(), path(), payload(), type(), errorType(), this)
    }

    /**
     * Default HTTP method.
     *
     * @return HTTP method as a string.
     */
    open fun method(): String {
        return GET
    }

    /**
     * Abstract method that needs to be implemented by all children of this class
     * to provide API path.
     *
     * @return the URL path as [HttpUrl].
     */
    protected abstract fun path(): HttpUrl

    /**
     * Additional parameters for the request, which is null by default for requests
     * that do not accept params (e.g. GET).
     *
     * @return the params as a [RequestBody].
     */
    open fun payload(): RequestBody? {
        // Has to be null as it would fail for GET requests
        return null
    }

    /**
     * Abstract method that needs to be implemented by all children of this class to
     * provide response type.
     *
     * @return Class type of response.
     */
    protected abstract fun type(): Class<T>

    /**
     * Abstract method that needs to be implemented by all children of this class to
     * provide error type.
     *
     * @return Class type of error.
     */
    open fun errorType(): Class<Error> = APIError::class.java as Class<Error>

    /**
     * Builds and returns a valid [HttpUrl] pointing to the given [Endpoint]'s host
     * and with all the supplied segments concatenated.
     *
     * @param endpoint The Omise API [Endpoint] to point to.
     * @param path The base API path.
     * @return An [HttpUrl] instance.
     */
    protected fun buildUrl(
        endpoint: Endpoint,
        path: String,
        vararg segments: String,
    ): HttpUrl {
        return HttpUrlBuilder(endpoint, path, segments.asList()).build()
    }

    /**
     * Serializes all the enclosed parameters in a child RequestBuilder. This method should be called
     * in the return statement of the overridden payload() method.
     *
     * @return the [RequestBody]
     * @throws IOException the I/O when [Serializer] is unable to correctly serialize the content of
     * the class using Jackson.
     */
    @Throws(IOException::class)
    protected fun serialize(): RequestBody {
        val stream = ByteArrayOutputStream(4096)
        serializer().serializeRequestBuilder(stream, this)
        return stream.toByteArray().toRequestBody(JSON_MEDIA_TYPE)
    }

    private fun serializer(): Serializer {
        return Serializer()
    }

    inner class HttpUrlBuilder(private val endpoint: Endpoint, private val path: String, private val segments: List<String>) {
        fun build(): HttpUrl {
            requireNonNull(endpoint)
            requireNonNull(path)
            val builder: HttpUrl.Builder = endpoint.buildUrl().addPathSegment(path)

            segments.forEach { segment ->
                if (segment.isNotBlank()) {
                    builder.addPathSegment(segment)
                }
            }

            return builder.build()
        }
    }

    companion object {
        const val POST = "POST"
        const val GET = "GET"
        const val PATCH = "PATCH"
        const val DELETE = "DELETE"
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
