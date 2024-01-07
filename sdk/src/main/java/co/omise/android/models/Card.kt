package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

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
    val bank: String? = null,
    @field:JsonProperty("tokenization_method")
    val tokenizationMethod: String? = null,
    override var modelObject: String? = null,
    override var id: String? = null,
    override var livemode: Boolean = false,
    override var location: String? = null,
    override var created: DateTime? = null,
    override var deleted: Boolean = false,
) : Model
