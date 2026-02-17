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
    @JsonValue open val name: String?,
) {
    object Alipay : SourceType("alipay")

    object BillPaymentTescoLotus : SourceType("bill_payment_tesco_lotus")

    object BarcodeAlipay : SourceType("barcode_alipay")

    object Econtext : SourceType("econtext")

    object TrueMoney : SourceType("truemoney")

    object TrueMoneyJumpApp : SourceType("truemoney_jumpapp")

    object PayNow : SourceType("paynow")

    object PromptPay : SourceType("promptpay")

    object AlipayCn : SourceType("alipay_cn")

    object AlipayHk : SourceType("alipay_hk")

    object Dana : SourceType("dana")

    object Gcash : SourceType("gcash")

    object Kakaopay : SourceType("kakaopay")

    data class TouchNGo(val provider: String? = null) : SourceType("touch_n_go")

    data class Fpx(var banks: List<Bank>? = null) : SourceType("fpx")

    object RabbitLinePay : SourceType("rabbit_linepay")

    object OcbcDigital : SourceType("mobile_banking_ocbc")

    object Boost : SourceType("boost")

    object ShopeePay : SourceType("shopeepay")

    object ShopeePayJumpApp : SourceType("shopeepay_jumpapp")

    object DuitNowOBW : SourceType("duitnow_obw")

    object DuitNowQR : SourceType("duitnow_qr")

    object MaybankQR : SourceType("maybank_qr")

    object Atome : SourceType("atome")

    data class GrabPay(val provider: String? = null) : SourceType("grabpay")

    object PayPay : SourceType("paypay")

    object WeChatPay : SourceType("wechat_pay")

    data class Unknown(override val name: String?) : SourceType(name)



    sealed class MobileBanking(
        @JsonValue override val name: String?,
    ) : SourceType(name) {
        object Bay : MobileBanking("mobile_banking_bay")

        object Bbl : MobileBanking("mobile_banking_bbl")

        object KBank : MobileBanking("mobile_banking_kbank")

        object KTB : MobileBanking("mobile_banking_ktb")

        object Scb : MobileBanking("mobile_banking_scb")

        data class Unknown(
            @JsonValue override val name: String?,
        ) : MobileBanking(name)
    }

    sealed class Installment(
        @JsonValue override val name: String?,
    ) : SourceType(name) {
        object Bay : Installment("installment_bay")

        object BayWlb : Installment("installment_wlb_bay")

        object FirstChoice : Installment("installment_first_choice")

        object FirstChoiceWlb : Installment("installment_wlb_first_choice")

        object Bbl : Installment("installment_bbl")

        object BblWlb : Installment("installment_wlb_bbl")

        object Mbb : Installment("installment_mbb")

        object Ktc : Installment("installment_ktc")

        object KtcWlb : Installment("installment_wlb_ktc")

        object KBank : Installment("installment_kbank")

        object KBankWlb : Installment("installment_wlb_kbank")

        object Scb : Installment("installment_scb")

        object ScbWlb : Installment("installment_wlb_scb")

        object Ttb : Installment("installment_ttb")

        object TtbWlb : Installment("installment_wlb_ttb")

        object Uob : Installment("installment_uob")

        object UobWlb : Installment("installment_wlb_uob")

        data class Unknown(
            @JsonValue override val name: String?,
        ) : Installment(name)
    }

    companion object {
        @SuppressLint("DefaultLocale")
        @JsonCreator
        @JvmStatic
        fun creator(name: String?): SourceType =
            when (name) {

                "mobile_banking_bay" -> MobileBanking.Bay
                "mobile_banking_bbl" -> MobileBanking.Bbl
                "mobile_banking_kbank" -> MobileBanking.KBank
                "mobile_banking_ktb" -> MobileBanking.KTB
                "mobile_banking_scb" -> MobileBanking.Scb
                "alipay" -> Alipay
                "bill_payment_tesco_lotus" -> BillPaymentTescoLotus
                "barcode_alipay" -> BarcodeAlipay
                "econtext" -> Econtext
                "fpx" -> Fpx()
                "truemoney" -> TrueMoney
                "truemoney_jumpapp" -> TrueMoneyJumpApp
                "installment_bay" -> Installment.Bay
                "installment_wlb_bay" -> Installment.BayWlb
                "installment_first_choice" -> Installment.FirstChoice
                "installment_wlb_first_choice" -> Installment.FirstChoiceWlb
                "installment_bbl" -> Installment.Bbl
                "installment_wlb_bbl" -> Installment.BblWlb
                "installment_mbb" -> Installment.Mbb
                "installment_ktc" -> Installment.Ktc
                "installment_wlb_ktc" -> Installment.KtcWlb
                "installment_kbank" -> Installment.KBank
                "installment_wlb_kbank" -> Installment.KBankWlb
                "installment_scb" -> Installment.Scb
                "installment_wlb_scb" -> Installment.ScbWlb
                "installment_ttb" -> Installment.Ttb
                "installment_wlb_ttb" -> Installment.TtbWlb
                "installment_uob" -> Installment.Uob
                "installment_wlb_uob" -> Installment.UobWlb
                "paynow" -> PayNow
                "promptpay" -> PromptPay
                "alipay_cn" -> AlipayCn
                "alipay_hk" -> AlipayHk
                "dana" -> Dana
                "gcash" -> Gcash
                "kakaopay" -> Kakaopay
                "touch_n_go" -> TouchNGo()
                "rabbit_linepay" -> RabbitLinePay
                "mobile_banking_ocbc" -> OcbcDigital
                "boost" -> Boost
                "shopeepay" -> ShopeePay
                "shopeepay_jumpapp" -> ShopeePayJumpApp
                "duitnow_obw" -> DuitNowOBW
                "duitnow_qr" -> DuitNowQR
                "maybank_qr" -> MaybankQR
                "grabpay" -> GrabPay()
                "paypay" -> PayPay
                "atome" -> Atome
                "wechat_pay" -> WeChatPay
                else -> Unknown(name)
            }
    }
}

object SourceTypeParceler : Parceler<SourceType> {
    override fun create(parcel: Parcel): SourceType {
        return SourceType.creator(parcel.readString())
    }

    override fun SourceType.write(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeString(name)
    }
}

val SourceType.Companion.allElements: List<SourceType>
    get() =
        listOf(

            SourceType.Alipay,
            SourceType.BillPaymentTescoLotus,
            SourceType.BarcodeAlipay,
            SourceType.Econtext,
            SourceType.Fpx(),
            SourceType.TrueMoney,
            SourceType.Installment.Bay,
            SourceType.Installment.BayWlb,
            SourceType.Installment.FirstChoice,
            SourceType.Installment.FirstChoiceWlb,
            SourceType.Installment.Bbl,
            SourceType.Installment.BblWlb,
            SourceType.Installment.Mbb,
            SourceType.Installment.Ktc,
            SourceType.Installment.KtcWlb,
            SourceType.Installment.KBank,
            SourceType.Installment.KBankWlb,
            SourceType.Installment.Scb,
            SourceType.Installment.ScbWlb,
            SourceType.Installment.Ttb,
            SourceType.Installment.TtbWlb,
            SourceType.Installment.Uob,
            SourceType.Installment.UobWlb,
            SourceType.Atome,
        )

sealed class SupportedEcontext : Parcelable {
    @Parcelize
    object ConvenienceStore : SupportedEcontext()

    @Parcelize
    object PayEasy : SupportedEcontext()

    @Parcelize
    object Netbanking : SupportedEcontext()
}
