package co.omise.android.api

import co.omise.android.models.Model
import okhttp3.HttpUrl
import okhttp3.RequestBody

/**
 * Request class acts as a holder class that encapsulate the information regarding which of the Omise APIs
 * the user wants to access and pass any additional data needed for that api request
 *
 * @param <T> the generic type for any Model that would need to be returned by the {@link Client} when this request is passed to it
 */
open class Request<T : Model>(
        internal val method: String,
        internal val url: HttpUrl,
        internal val payload: RequestBody?,
        internal val responseType: Class<T>
)