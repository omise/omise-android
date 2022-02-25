package co.omise.android.models

import android.annotation.SuppressLint
import android.os.Parcel
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.android.parcel.Parceler

/**
 * Represents Tokenization Method object.
 *
 * @see [Tokens API](https://www.omise.co/tokens-api)
 */
sealed class TokenizationMethod(
        @JsonValue open val name: String?
) {
    object Card : TokenizationMethod("card")
    object GooglePay : TokenizationMethod("googlepay")
    data class Unknown(override val name: String?) : TokenizationMethod(name)

    companion object {
        @SuppressLint("DefaultLocale")
        @JsonCreator
        @JvmStatic
        fun creator(name: String?): TokenizationMethod = when (name) {
            "card" -> Card
            "googlepay" -> GooglePay
            else -> Unknown(name)
        }
    }
}

object TokenizationMethodParceler : Parceler<TokenizationMethod> {
    override fun create(parcel: Parcel): TokenizationMethod {
        return TokenizationMethod.creator(parcel.readString())
    }

    override fun TokenizationMethod.write(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }
}

val TokenizationMethod.Companion.allElements: List<TokenizationMethod>
    get() = listOf(
            TokenizationMethod.GooglePay,
            TokenizationMethod.Card
    )
