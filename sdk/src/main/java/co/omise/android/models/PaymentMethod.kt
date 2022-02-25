package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

/**
 * PaymentMethod contains all the information regarding a payment method available to the
 * user. PaymentMethod list can be found in the [Capability] object.
 *
 * @see [Capabilities API](https://www.omise.co/capability-api)
 */
@Parcelize
data class PaymentMethod(
        var name: String? = null,
        var currencies: List<String>? = null,
        @field:JsonProperty("card_brands")
        var cardBrands: List<String>? = null,
        @field:JsonProperty("installment_terms")
        var installmentTerms: List<Int>? = null,
        var banks: List<Bank>? = null,
        override var modelObject: String? = null,
        override var id: String? = null,
        override var livemode: Boolean = false,
        override var location: String? = null,
        override var created: DateTime? = null,
        override var deleted: Boolean = false
) : Model {

    companion object {
        @JvmStatic
        fun createCreditCardMethod(): PaymentMethod =
                PaymentMethod(name = "card")

        @JvmStatic
        fun createSourceTypeMethod(sourceType: SourceType): PaymentMethod =
                PaymentMethod(
                        name = sourceType.name,
                        installmentTerms = when (sourceType) {
                            is SourceType.Installment -> SourceType.Installment.availableTerms(sourceType)
                            else -> null
                        },
                        banks = when (sourceType) {
                            is SourceType.Fpx -> SourceType.Fpx.banks
                            else -> null
                        }
                )

        @JvmStatic
        fun createTokenizationMethod(tokenizationMethod: TokenizationMethod): PaymentMethod =
                PaymentMethod(
                        name = tokenizationMethod.name,
                )
    }
}

val PaymentMethod.backendType: BackendType
    get() = when (name) {
        "card" -> BackendType.Token(TokenizationMethod.creator(name))
        "googlepay" -> BackendType.Token(TokenizationMethod.creator(name))
        else -> BackendType.Source(SourceType.creator(name))
    }

sealed class BackendType(open val name: String?) {
    data class Token(val tokenizationMethod: TokenizationMethod) : BackendType(tokenizationMethod.name)
    data class Source(val sourceType: SourceType) : BackendType(sourceType.name)
}
