package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Card(
        @JvmField
        val country: String? = null,
        @JvmField
        val city: String? = null,
        @JvmField
        @field:JsonProperty("postal_code")
        val postalCode: String? = null,
        @JvmField
        val financing: String? = null,
        @JvmField
        @field:JsonProperty("last_digits")
        val lastDigits: String? = null,
        @JvmField
        val brand: String? = null,
        @JvmField
        @field:JsonProperty("expiration_month")
        val expirationMonth: Int = 0,
        @JvmField
        @field:JsonProperty("expiration_year")
        val expirationYear: Int = 0,
        @JvmField
        val fingerprint: String? = null,
        @JvmField
        val name: String? = null,
        @JvmField
        @field:JsonProperty("security_code_check")
        val securityCodeCheck: Boolean = false,
        @JvmField
        val bank: String? = null
) : Model()
