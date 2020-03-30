package co.omise.android.models

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

/**
 * Represents Source Type object.
 *
 * @see [Sources API](https://www.omise.co/sources-api)
 */
sealed class SourceType(
        @JsonValue open val name: String?
) {
    object Alipay : SourceType("alipay")
    object BillPaymentTescoLotus : SourceType("bill_payment_tesco_lotus")
    object BarcodeAlipay : SourceType("barcode_alipay")
    object Econtext : SourceType("econtext")
    object TrueMoney : SourceType("truemoney")
    object PointsCiti: SourceType("points_citi")
    data class Unknown(override val name: String?) : SourceType(name)

    sealed class InternetBanking(@JsonValue override val name: String?) : SourceType(name) {
        object Bay : InternetBanking("internet_banking_bay")
        object Ktb : InternetBanking("internet_banking_ktb")
        object Scb : InternetBanking("internet_banking_scb")
        object Bbl : InternetBanking("internet_banking_bbl")
        data class Unknown(@JsonValue override val name: String?) : InternetBanking(name)
    }

    sealed class Installment(@JsonValue override val name: String?) : SourceType(name) {
        object Bay : Installment("installment_bay")
        object FirstChoice : Installment("installment_first_choice")
        object Bbl : Installment("installment_bbl")
        object Ktc : Installment("installment_ktc")
        object KBank : Installment("installment_kbank")
        data class Unknown(@JsonValue override val name: String?) : Installment(name)

        companion object {
            fun availableTerms(installment: Installment): List<Int> =
                    when (installment) {
                        Bay -> listOf(3, 4, 6, 9, 10)
                        FirstChoice -> listOf(3, 4, 6, 9, 10, 12, 18, 24, 36)
                        Bbl -> listOf(4, 6, 8, 9, 10)
                        Ktc -> listOf(3, 4, 5, 6, 7, 8, 9, 10)
                        KBank -> listOf(3, 4, 6, 10)
                        is Unknown -> emptyList()
                    }
        }
    }

    companion object {
        @SuppressLint("DefaultLocale")
        @JsonCreator
        @JvmStatic
        fun creator(name: String?): SourceType = when (name) {
            "internet_banking_bay" -> InternetBanking.Bay
            "internet_banking_ktb" -> InternetBanking.Ktb
            "internet_banking_scb" -> InternetBanking.Scb
            "internet_banking_bbl" -> InternetBanking.Bbl
            "alipay" -> Alipay
            "bill_payment_tesco_lotus" -> BillPaymentTescoLotus
            "barcode_alipay" -> BarcodeAlipay
            "econtext" -> Econtext
            "truemoney" -> TrueMoney
            "installment_bay" -> Installment.Bay
            "installment_first_choice" -> Installment.FirstChoice
            "installment_bbl" -> Installment.Bbl
            "installment_ktc" -> Installment.Ktc
            "installment_kbank" -> Installment.KBank
            "points_citi" -> PointsCiti
            else -> Unknown(name)
        }
    }
}

object SourceTypeParceler : Parceler<SourceType> {
    override fun create(parcel: Parcel): SourceType {
        return SourceType.creator(parcel.readString())
    }

    override fun SourceType.write(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }
}

val SourceType.Companion.allElements: List<SourceType>
    get() = listOf(
            SourceType.InternetBanking.Bay,
            SourceType.InternetBanking.Ktb,
            SourceType.InternetBanking.Scb,
            SourceType.InternetBanking.Bbl,
            SourceType.Alipay,
            SourceType.BillPaymentTescoLotus,
            SourceType.BarcodeAlipay,
            SourceType.Econtext,
            SourceType.TrueMoney,
            SourceType.Installment.Bay,
            SourceType.Installment.FirstChoice,
            SourceType.Installment.Bbl,
            SourceType.Installment.Ktc,
            SourceType.Installment.KBank,
            SourceType.PointsCiti
    )

sealed class SupportedEcontext : Parcelable {
    @Parcelize
    object ConvenienceStore : SupportedEcontext()

    @Parcelize
    object PayEasy : SupportedEcontext()

    @Parcelize
    object Netbanking : SupportedEcontext()
}