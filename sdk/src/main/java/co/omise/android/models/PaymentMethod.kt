package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

@Parcelize
data class PaymentMethod(
        var name: String? = null,
        var currencies: List<String>? = null,
        @field:JsonProperty("card_brands")
        var cardBrands: List<String>? = null,
        @field:JsonProperty("installment_terms")
        val installmentTerms: List<Int>? = null,
        override var modelObject: String? = null,
        override var id: String? = null,
        override var livemode: Boolean = false,
        override var location: String? = null,
        override var created: DateTime? = null,
        override var deleted: Boolean = false
) : Model

val PaymentMethod.backendType: BackendType
    get() = when (name) {
        "card" -> BackendType.Token
        else -> BackendType.Source(SourceType.creator(name))
    }

sealed class BackendType(open val name: String?) {
    object Token : BackendType("card")
    data class Source(val sourceType: SourceType) : BackendType(sourceType.name)
}
