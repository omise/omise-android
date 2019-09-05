package co.omise.android.models

import co.omise.android.api.Endpoint
import co.omise.android.api.RequestBuilder
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import okhttp3.HttpUrl
import okhttp3.RequestBody
import org.joda.time.DateTime
import java.io.IOException

/**
 * Represents Token object and contains its [RequestBuilder].
 *
 * @see [Token API](https://www.omise.co/tokens-api)
 */
@Parcelize
data class Token(
        val used: Boolean = false,
        val card: Card? = null,
        override var modelObject: String? = null,
        override var id: String? = null,
        override var livemode: Boolean = false,
        override var location: String? = null,
        override var created: DateTime? = null,
        override var deleted: Boolean = false
) : Model {

    /**
     * The [RequestBuilder] class for creating a Token.
     */
    class CreateTokenRequestBuilder(
            @field:JsonProperty("card")
            val card: CardParam
    ) : RequestBuilder<Token>() {
        override fun path(): HttpUrl {
            return buildUrl(Endpoint.VAULT, "tokens")
        }

        @Throws(IOException::class)
        override fun payload(): RequestBody? {
            return serialize()
        }

        override fun method(): String {
            return POST
        }

        override fun type(): Class<Token> {
            return Token::class.java
        }
    }
}
