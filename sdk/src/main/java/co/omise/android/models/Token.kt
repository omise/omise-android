package co.omise.android.models

import android.os.Parcel
import android.os.Parcelable
import co.omise.android.api.Endpoint
import co.omise.android.api.RequestBuilder
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.RequestBody

/**
 * Represents Token object and contains its [RequestBuilder].
 *
 * @see [Token API](https://www.omise.co/tokens-api)
 */
data class Token(
        var used: Boolean = false,
        var card: Card? = null
) : Model(), Parcelable {

    /**
     * The [RequestBuilder] class for creating a Token.
     */
    class CreateTokenRequestBuilder(
            private var name: String,
            private var number: String,
            private var expirationMonth: Int,
            private var expirationYear: Int,
            private var securityCode: String
    ) : RequestBuilder<Token>() {
        private var city: String? = null
        private var postalCode: String? = null

        override fun path(): HttpUrl {
            return buildUrl(Endpoint.VAULT, "tokens")
        }

        override fun payload(): RequestBody? {
            val builder = FormBody.Builder()
                    .add("card[name]", name)
                    .add("card[number]", number)
                    .add("card[expiration_month]", Integer.toString(expirationMonth))
                    .add("card[expiration_year]", Integer.toString(expirationYear))
                    .add("card[security_code]", securityCode)

            if (city != null && !city!!.isEmpty()) {
                builder.add("card[city]", city!!)
            }
            if (postalCode != null && !postalCode!!.isEmpty()) {
                builder.add("card[postal_code]", postalCode!!)
            }

            return builder.build()
        }

        override fun method(): String {
            return RequestBuilder.POST
        }

        override fun type(): Class<Token> {
            return Token::class.java
        }

        fun city(city: String): CreateTokenRequestBuilder {
            this.city = city
            return this
        }

        fun postalCode(postalCode: String): CreateTokenRequestBuilder {
            this.postalCode = postalCode
            return this
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
