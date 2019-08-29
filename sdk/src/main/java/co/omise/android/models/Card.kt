package co.omise.android.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

/**
 * Represents Card object.
 *
 * @see <a href="https://www.omise.co/cards-api">Card API</a>
 */
@Parcelize
data class Card(
        val country: String? = null,
        val city: String? = null,
        @field:JsonProperty("postal_code")
        val postalCode: String? = null,
        val financing: String? = null,
        @field:JsonProperty("last_digits")
        val lastDigits: String? = null,
        val brand: String? = null,
        @field:JsonProperty("expiration_month")
        val expirationMonth: Int = 0,
        @field:JsonProperty("expiration_year")
        val expirationYear: Int = 0,
        val fingerprint: String? = null,
        val name: String? = null,
        @field:JsonProperty("security_code_check")
        val securityCodeCheck: Boolean = false,
        val bank: String? = null
) : Model(), Parcelable
