package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * TokenizationParam holds all the information about a tokenized card that
 * can be supplied to create a [Token].
 *
 * @see [Token API](https://www.omise.co/tokens-api#create_from_tokenized_card)
 */
data class TokenizationParam(
    @field:JsonProperty
    val method: String,
    @field:JsonProperty
    val data: String,
    @field:JsonProperty("billing_name")
    val billingName: String? = null,
    @field:JsonProperty("billing_city")
    val billingCity: String? = null,
    @field:JsonProperty("billing_country")
    val billingCountry: String? = null,
    @field:JsonProperty("billing_postal_code")
    val billingPostalCode: String? = null,
    @field:JsonProperty("billing_state")
    val billingState: String? = null,
    @field:JsonProperty("billing_street1")
    val billingStreet1: String? = null,
    @field:JsonProperty("billing_street2")
    val billingStreet2: String? = null,
    @field:JsonProperty("billing_phone_number")
    val billingPhoneNumber: String? = null,
)
