package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentMethod(
        @JvmField
        var name: String? = null,
        @JvmField
        var currencies: List<String>? = null,
        @JvmField
        @field:JsonProperty("card_brands")
        var cardBrands: List<String>? = null,
        @JvmField
        @field:JsonProperty("installment_terms")
        var installmentTerms: List<Int>? = null
) : Model()
