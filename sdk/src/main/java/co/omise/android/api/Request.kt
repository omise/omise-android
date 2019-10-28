package co.omise.android.api

import co.omise.android.models.Model
import okhttp3.HttpUrl
import okhttp3.RequestBody

/**
 * Request class acts as a holder class that encapsulates the information regarding which
 * of the Omise APIs the user wants to access and pass any additional data needed for that api request
 *
 * @param <T> the generic type for any Model that would need to be returned by the [Client] when this
 * request is passed to it.
 * @param method HTTP method.
 * @param url API URL.
 * @param payload Additional optional data to be sent with the Request.
 * @param responseType Response class type.
 */
open class Request<T : Model>(
        internal val method: String,
        internal val url: HttpUrl,
        internal val payload: RequestBody?,
        internal val responseType: Class<T>
)
