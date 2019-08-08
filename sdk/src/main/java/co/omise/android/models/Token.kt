package co.omise.android.models

import android.os.Parcel
import android.os.Parcelable
import co.omise.android.api.Endpoint
import co.omise.android.api.RequestBuilder
import com.fasterxml.jackson.annotation.JsonProperty
import okhttp3.HttpUrl
import okhttp3.RequestBody
import java.io.IOException

/**
 * Represents Token object and contains its [RequestBuilder].
 *
 * @see [Token API](https://www.omise.co/tokens-api)
 */
data class Token(
        var used: Boolean = false,
        var card: Card? = null
) : Model() {

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
            return RequestBuilder.POST
        }

        override fun type(): Class<Token> {
            return Token::class.java
        }
    }

    constructor(parcel: Parcel) : this() {
        used = parcel.readInt() == 1
        card = parcel.readParcelable(Card::class.java.classLoader)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if (used) 1 else 0)
        dest.writeParcelable(card, 0)
    }

    companion object CREATOR : Parcelable.Creator<Token> {
        override fun createFromParcel(parcel: Parcel): Token {
            return Token(parcel)
        }

        override fun newArray(size: Int): Array<Token?> {
            return arrayOfNulls(size)
        }
    }
}
