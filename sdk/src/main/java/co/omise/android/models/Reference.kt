package co.omise.android.models

import java.util.*

class Reference {
    @JsonProperty("va_code")
    val vaCode: String? = null
    @JsonProperty("omise_tax_id")
    val omiseTaxId: String? = null
    @JsonProperty("reference_number_1")
    val referenceNumber1: String? = null
    @JsonProperty("reference_number_2")
    val referenceNumber2: String? = null
    val barcode: String? = null
    @JsonProperty("expires_at")
    val expiresAt: Date? = null
}