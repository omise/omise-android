package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * CardParam holds all the information about a Card that can be supplied to create
 * a [Token].
 *
 * @see [Token API](https://www.omise.co/tokens-api)
 */
data class CardParam(
    @field:JsonProperty
    val name: String,
    @field:JsonProperty
    val number: String,
    @field:JsonProperty("expiration_month")
    val expirationMonth: Int,
    @field:JsonProperty("expiration_year")
    val expirationYear: Int,
    @field:JsonProperty("security_code")
    val securityCode: String,
    @field:JsonProperty
    val city: String? = null,
    @field:JsonProperty
    val state: String? = null,
    @field:JsonProperty("postal_code")
    val postalCode: String? = null,
    @field:JsonProperty
    val country: String? = null,
    @field:JsonProperty
    val street1: String? = null,
    @field:JsonProperty
    val street2: String? = null,
    @field:JsonProperty("phone_number")
    val phoneNumber: String? = null,
)
