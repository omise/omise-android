package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonProperty

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
        @field:JsonProperty("postal_code")
        val postalCode: String? = null
)