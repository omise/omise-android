package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

@Parcelize
data class PaymentMethod(
        val name: String? = null,
        val currencies: List<String>? = null,
        @field:JsonProperty("card_brands")
        val cardBrands: List<String>? = null,
        @field:JsonProperty("installment_terms")
        val installmentTerms: List<Int>? = null,
        override var modelObject: String? = null,
        override var id: String? = null,
        override var livemode: Boolean = false,
        override var location: String? = null,
        override var created: DateTime? = null,
        override var deleted: Boolean = false
) : Model
