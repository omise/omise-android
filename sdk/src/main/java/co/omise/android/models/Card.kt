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
        var country: String? = null,
        var city: String? = null,
        @field:JsonProperty("postal_code")
        var postalCode: String? = null,
        var financing: String? = null,
        @field:JsonProperty("last_digits")
        var lastDigits: String? = null,
        var brand: String? = null,
        @field:JsonProperty("expiration_month")
        var expirationMonth: Int = 0,
        @field:JsonProperty("expiration_year")
        var expirationYear: Int = 0,
        var fingerprint: String? = null,
        var name: String? = null,
        @field:JsonProperty("security_code_check")
        var securityCodeCheck: Boolean = false,
        var bank: String? = null
) : Model(), Parcelable
