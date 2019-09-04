package co.omise.android.models

import android.os.Parcelable
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

val PaymentMethod.method: PaymentMethodType
    get() = when {

        name == "card" -> PaymentMethodType.Card(cardBrands ?: emptyList())
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

sealed class PaymentMethodType : Parcelable {
    companion object
    @Parcelize
    data class Card(val brands: List<String>) : PaymentMethodType()

    sealed class InternetBanking(val value: String) : PaymentMethodType(), Parcelable {
        @Parcelize
        object Bay : InternetBanking("internet_banking_bay")

        @Parcelize
        object Bbl : InternetBanking("internet_banking_bbl")

        @Parcelize
        object Ktb : InternetBanking("internet_banking_ktb")

        @Parcelize
        object Scb : InternetBanking("internet_banking_scb")

        @Parcelize
        data class Unknown(val name: String?) : InternetBanking(name.orEmpty())
    }

    sealed class Installment(val value: String, val availableTerms: List<Int>) : PaymentMethodType(), Parcelable {
        @Parcelize
        object Bbl : Installment("installment_bay", listOf(4, 6, 8, 9, 10))

        @Parcelize
        object Kbank : Installment("installment_kbank", listOf(3, 4, 6, 10))

        @Parcelize
        object Bay : Installment("installment_bay", listOf(3, 4, 6, 9, 10))

        @Parcelize
        object FirstChoice : Installment("installment_first_choice", listOf(3, 4, 6, 9, 10, 12, 18, 24, 36))

        @Parcelize
        object Ktc : Installment("installment_ktc", listOf(3, 4, 5, 6, 7, 8, 9, 10))

        @Parcelize
        data class Unknown(val name: String?, val terms: List<Int>) : Installment(name.orEmpty(), terms)
    }

    @Parcelize
    object Alipay : PaymentMethodType()

    @Parcelize
    object BarcodeAlipay : PaymentMethodType()

    @Parcelize
    object BillPaymentTescoLotus : PaymentMethodType()

    @Parcelize
    object EContext : PaymentMethodType()

    @Parcelize
    object Points : PaymentMethodType()

    @Parcelize
    object Truemoney : PaymentMethodType()

    @Parcelize
    data class Unknown(val name: String?) : PaymentMethodType()
}
