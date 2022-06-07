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
    object PointsCiti : SourceType("points_citi")
    object PayNow : SourceType("paynow")
    object PromptPay : SourceType("promptpay")
    object AlipayCn : SourceType("alipay_cn")
    object AlipayHk : SourceType("alipay_hk")
    object Dana : SourceType("dana")
    object Gcash : SourceType("gcash")
    object Kakaopay : SourceType("kakaopay")
    object TouchNGo : SourceType("touch_n_go")
    object Fpx : SourceType("fpx") {
        var banks : List<Bank>? = null
    }
    object RabbitLinePay : SourceType("rabbit_linepay")
    object OcbcPao : SourceType("mobile_banking_ocbc_pao")
    object Boost: SourceType("boost")
    object ShopeePay: SourceType("shopeepay")
    object DuitnowOBW  : SourceType("duitnow_obw")
    object DuitnowQR : SourceType("duitnow_qr")
    object MaybankQR : SourceType("maybank_qr")

    data class Unknown(override val name: String?) : SourceType(name)

    sealed class InternetBanking(@JsonValue override val name: String?) : SourceType(name) {
        object Bay : InternetBanking("internet_banking_bay")
        object Ktb : InternetBanking("internet_banking_ktb")
        object Scb : InternetBanking("internet_banking_scb")
        object Bbl : InternetBanking("internet_banking_bbl")
        data class Unknown(@JsonValue override val name: String?) : InternetBanking(name)
    }

    sealed class MobileBanking(@JsonValue override val name: String?) : SourceType(name) {
        object Bay : MobileBanking("mobile_banking_bay")
        object Bbl : MobileBanking("mobile_banking_bbl")
        object KBank : MobileBanking("mobile_banking_kbank")
        object Scb : MobileBanking("mobile_banking_scb")
        data class Unknown(@JsonValue override val name: String?) : MobileBanking(name)
    }

    sealed class Installment(@JsonValue override val name: String?) : SourceType(name) {
        object Bay : Installment("installment_bay")
        object FirstChoice : Installment("installment_first_choice")
        object Bbl : Installment("installment_bbl")
        object Ezypay : Installment("installment_ezypay")
        object Ktc : Installment("installment_ktc")
        object KBank : Installment("installment_kbank")
        object Scb : Installment("installment_scb")
        object Citi : Installment("installment_citi")
        object Ttb : Installment("installment_ttb")
        object Uob : Installment("installment_uob")
        data class Unknown(@JsonValue override val name: String?) : Installment(name)

        companion object {
            fun availableTerms(installment: Installment): List<Int> =
                    when (installment) {
                        Bay -> listOf(3, 4, 6, 9, 10)
                        FirstChoice -> listOf(3, 4, 6, 9, 10, 12, 18, 24, 36)
                        Bbl -> listOf(4, 6, 8, 9, 10)
                        Ezypay -> listOf(6, 12, 24)
                        Ktc -> listOf(3, 4, 5, 6, 7, 8, 9, 10)
                        KBank -> listOf(3, 4, 6, 10)
                        Scb -> listOf(3, 4, 6, 9, 10)
                        Citi -> listOf(4, 5, 6, 7, 8, 9, 10)
                        Ttb -> listOf(3, 4, 5, 6, 7, 8, 9, 10)
                        Uob -> listOf(4, 5, 6, 7, 8, 9, 10)
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
            "mobile_banking_bay" -> MobileBanking.Bay
            "mobile_banking_bbl" -> MobileBanking.Bbl
            "mobile_banking_kbank" -> MobileBanking.KBank
            "mobile_banking_scb" -> MobileBanking.Scb
            "alipay" -> Alipay
            "bill_payment_tesco_lotus" -> BillPaymentTescoLotus
            "barcode_alipay" -> BarcodeAlipay
            "econtext" -> Econtext
            "fpx" -> Fpx
            "truemoney" -> TrueMoney
            "installment_bay" -> Installment.Bay
            "installment_first_choice" -> Installment.FirstChoice
            "installment_bbl" -> Installment.Bbl
            "installment_ezypay" -> Installment.Ezypay
            "installment_ktc" -> Installment.Ktc
            "installment_kbank" -> Installment.KBank
            "installment_scb" -> Installment.Scb
            "installment_citi" -> Installment.Citi
            "installment_ttb" -> Installment.Ttb
            "installment_uob" -> Installment.Uob
            "points_citi" -> PointsCiti
            "paynow" -> PayNow
            "promptpay" -> PromptPay
            "alipay_cn" -> AlipayCn
            "alipay_hk" -> AlipayHk
            "dana" -> Dana
            "gcash" -> Gcash
            "kakaopay" -> Kakaopay
            "touch_n_go" -> TouchNGo
            "rabbit_linepay" -> RabbitLinePay
            "mobile_banking_ocbc_pao" -> OcbcPao
            "boost" -> Boost
            "shopeepay" -> ShopeePay
            "duitnow_obw" -> DuitnowOBW
            "duitnow_qr" -> DuitnowQR
            "maybank_qr" -> MaybankQR
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
            SourceType.Fpx,
            SourceType.TrueMoney,
            SourceType.Installment.Bay,
            SourceType.Installment.FirstChoice,
            SourceType.Installment.Bbl,
            SourceType.Installment.Ezypay,
            SourceType.Installment.Ktc,
            SourceType.Installment.KBank,
            SourceType.PointsCiti,
            SourceType.Installment.Scb,
            SourceType.Installment.Citi,
            SourceType.Installment.Ttb,
            SourceType.Installment.Uob

    )

sealed class SupportedEcontext : Parcelable {
    @Parcelize
    object ConvenienceStore : SupportedEcontext()

    @Parcelize
    object PayEasy : SupportedEcontext()

    @Parcelize
    object Netbanking : SupportedEcontext()
}
