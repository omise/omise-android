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

val PaymentMethod.method: PaymentMethodType
    get() = when {

        name == "card" -> PaymentMethodType.CreditCard(cardBrands ?: emptyList())
        name.orEmpty().startsWith("installment_") -> {
            when (name) {
                "installment_bay" -> PaymentMethodType.Installment.Bay
                "installment_bbl" -> PaymentMethodType.Installment.Bbl
                "installment_first_choice" -> PaymentMethodType.Installment.FirstChoice
                "installment_kbank" -> PaymentMethodType.Installment.Kbank
                "installment_ktc" -> PaymentMethodType.Installment.Ktc
                else -> PaymentMethodType.Installment.Unknown(name, installmentTerms.orEmpty())
            }
        }
        name.orEmpty().startsWith("internet_banking_") -> {
            when (name) {
                "internet_banking_bay" -> PaymentMethodType.InternetBanking.Bay
                "internet_banking_bbl" -> PaymentMethodType.InternetBanking.Bbl
                "internet_banking_ktb" -> PaymentMethodType.InternetBanking.Ktb
                "internet_banking_scb" -> PaymentMethodType.InternetBanking.Scb
                else -> PaymentMethodType.InternetBanking.Unknown(name)
            }
            PaymentMethodType.InternetBanking.Bay
        }
        name == "alipay" -> PaymentMethodType.Alipay
        name == "barcode_alipay" -> PaymentMethodType.BarcodeAlipay
        name == "bill_payment_tesco_lotus" -> PaymentMethodType.BillPaymentTescoLotus
        name == "econtext" -> PaymentMethodType.EContext
        name == "points" -> PaymentMethodType.Points
        name == "truemoney" -> PaymentMethodType.Truemoney
        else -> PaymentMethodType.Unknown(name)
    }

sealed class PaymentMethodType {
    data class CreditCard(val brands: List<String>) : PaymentMethodType()

    sealed class InternetBanking(val value: String) : PaymentMethodType() {
        object Bay : InternetBanking("internet_banking_bay")

        object Bbl : InternetBanking("internet_banking_bbl")

        object Ktb : InternetBanking("internet_banking_ktb")

        object Scb : InternetBanking("internet_banking_scb")

        data class Unknown(val name: String?) : InternetBanking(name.orEmpty())
    }

    sealed class Installment(val value: String, val availableTerms: List<Int>) : PaymentMethodType() {
        object Bbl : Installment("installment_bay", listOf(4, 6, 8, 9, 10))
        object Kbank : Installment("installment_kbank", listOf(3, 4, 6, 10))
        object Bay : Installment("installment_bay", listOf(3, 4, 6, 9, 10))
        object FirstChoice : Installment("installment_first_choice", listOf(3, 4, 6, 9, 10, 12, 18, 24, 36))
        object Ktc : Installment("installment_ktc", listOf(3, 4, 5, 6, 7, 8, 9, 10))
        data class Unknown(val name: String?, val terms: List<Int>) : Installment(name.orEmpty(), terms)
    }

    object Alipay : PaymentMethodType()
    object BarcodeAlipay : PaymentMethodType()
    object BillPaymentTescoLotus : PaymentMethodType()
    object EContext : PaymentMethodType()
    object Points : PaymentMethodType()
    object Truemoney : PaymentMethodType()
    data class Unknown(val name: String?) : PaymentMethodType()
}
