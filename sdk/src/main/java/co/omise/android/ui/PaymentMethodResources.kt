package co.omise.android.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import co.omise.android.R
import co.omise.android.models.*

internal val Capability.paymentMethodResources: List<PaymentMethodResource>
    get() {
        val items = mutableListOf<PaymentMethodResource>()
        this.paymentMethods
                .orEmpty()
                .forEach { paymentMethod ->
                    when (paymentMethod.backendType) {
                        BackendType.Token -> items.add(PaymentMethodResource.CreditCard)
                        is BackendType.Source -> when ((paymentMethod.backendType as BackendType.Source).sourceType) {
                            is SourceType.Installment -> items.add(PaymentMethodResource.Installments)
                            is SourceType.InternetBanking -> items.add(PaymentMethodResource.InternetBankings)
                            is SourceType.MobileBanking -> items.add(PaymentMethodResource.MobileBankings)
                            is SourceType.Econtext -> items.addAll(listOf(PaymentMethodResource.ConvenienceStore, PaymentMethodResource.PayEasy, PaymentMethodResource.Netbanking))
                            else -> PaymentMethodResource.all.find { it.sourceType == (paymentMethod.backendType as? BackendType.Source)?.sourceType }?.let { items.add(it) }
                        }
                    }
                }
        return items.distinct()
    }

internal val List<SourceType.Installment>.installmentResources: List<InstallmentResource>
    get() = this.mapNotNull { sourceType -> InstallmentResource.all.find { it.sourceType == sourceType } }

internal val List<SourceType.InternetBanking>.internetBankingResources: List<InternetBankingResource>
    get() = this.mapNotNull { sourceType -> InternetBankingResource.all.find { it.sourceType == sourceType } }

internal val List<SourceType.MobileBanking>.mobileBankingResources: List<MobileBankingResource>
    get() = this.mapNotNull { sourceType -> MobileBankingResource.all.find { it.sourceType == sourceType } }

internal sealed class PaymentMethodResource(
        @DrawableRes override val iconRes: Int,
        @StringRes override val titleRes: Int?,
        @DrawableRes override val indicatorIconRes: Int,
        val isCreditCard: Boolean = false,
        val sourceType: SourceType? = null
) : OmiseListItem {
    object CreditCard : PaymentMethodResource(
            iconRes = R.drawable.payment_card,
            titleRes = R.string.payment_method_credit_card_title,
            indicatorIconRes = R.drawable.ic_next,
            isCreditCard = true
    )

    object Installments : PaymentMethodResource(
            iconRes = R.drawable.payment_installment,
            titleRes = R.string.payment_method_installments_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object InternetBankings : PaymentMethodResource(
            iconRes = R.drawable.payment_banking,
            titleRes = R.string.payment_method_internet_banking_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object MobileBankings : PaymentMethodResource(
            iconRes = R.drawable.payment_mobile,
            titleRes = R.string.payment_method_mobile_banking_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object TescoLotus : PaymentMethodResource(
            iconRes = R.drawable.payment_tesco,
            titleRes = R.string.payment_method_tesco_lotus_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.BillPaymentTescoLotus
    )

    object ConvenienceStore : PaymentMethodResource(
            iconRes = R.drawable.payment_conbini,
            titleRes = R.string.payment_method_convenience_store_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Econtext
    )

    object PayEasy : PaymentMethodResource(
            iconRes = R.drawable.payment_payeasy,
            titleRes = R.string.payment_method_pay_easy_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Econtext
    )

    object Netbanking : PaymentMethodResource(
            iconRes = R.drawable.payment_netbank,
            titleRes = R.string.payment_method_netbank_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Econtext
    )

    object Alipay : PaymentMethodResource(
            iconRes = R.drawable.payment_alipay,
            titleRes = R.string.payment_method_alipay_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.Alipay
    )

    object PromptPay : PaymentMethodResource(
            iconRes = R.drawable.payment_promptpay,
            titleRes = R.string.payment_method_promptpay_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.PromptPay
    )

    object PayNow : PaymentMethodResource(
            iconRes = R.drawable.payment_paynow,
            titleRes = R.string.payment_method_paynow_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.PayNow
    )

    object PointsCiti : PaymentMethodResource(
            iconRes = R.drawable.payment_points_citi,
            titleRes = R.string.payment_method_points_citi_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.PointsCiti
    )

    object TrueMoney : PaymentMethodResource(
            iconRes = R.drawable.payment_truemoney,
            titleRes = R.string.payment_truemoney_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.TrueMoney
    )

    object Fpx : PaymentMethodResource(
            iconRes = R.drawable.payment_fpx,
            titleRes = R.string.payment_method_fpx_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Fpx
    )

    companion object {
        val all: List<PaymentMethodResource>
            get() = PaymentMethodResource::class.nestedClasses.mapNotNull { it.objectInstance as? PaymentMethodResource }
    }
}

internal sealed class InstallmentResource(
        @DrawableRes override val iconRes: Int,
        override val title: String? = null,
        @StringRes override val titleRes: Int? = null,
        @DrawableRes override val indicatorIconRes: Int,
        val sourceType: SourceType
) : OmiseListItem {
    companion object {
        val all: List<InstallmentResource>
            get() = InstallmentResource::class.nestedClasses.mapNotNull { it.objectInstance as? InstallmentResource }
    }

    object Bbl : InstallmentResource(
            iconRes = R.drawable.payment_bbl,
            titleRes = R.string.payment_method_installment_bbl_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Installment.Bbl
    )

    object KBank : InstallmentResource(
            iconRes = R.drawable.payment_kasikorn,
            titleRes = R.string.payment_method_installment_kasikorn_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Installment.KBank
    )

    object Bay : InstallmentResource(
            iconRes = R.drawable.payment_bay,
            titleRes = R.string.payment_method_installment_bay_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Installment.Bay
    )

    object FirstChoice : InstallmentResource(
            iconRes = R.drawable.payment_first_choice,
            titleRes = R.string.payment_method_installment_first_choice_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Installment.FirstChoice
    )

    object Ktc : InstallmentResource(
            iconRes = R.drawable.payment_ktc,
            titleRes = R.string.payment_method_installment_ktc_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Installment.Ktc
    )

    object Scb : InstallmentResource(
            iconRes = R.drawable.payment_scb,
            titleRes = R.string.payment_method_installment_scb_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Installment.Scb
    )
}

internal data class InstallmentTermResource(
        @DrawableRes override val iconRes: Int? = null,
        override val title: String,
        val installmentTerm: Int,
        @DrawableRes override val indicatorIconRes: Int = R.drawable.ic_redirect
) : OmiseListItem

internal sealed class InternetBankingResource(
        @DrawableRes override val iconRes: Int,
        override val title: String? = null,
        @StringRes override val titleRes: Int? = null,
        @DrawableRes override val indicatorIconRes: Int,
        val sourceType: SourceType
) : OmiseListItem {

    companion object {
        val all: List<InternetBankingResource>
            get() = InternetBankingResource::class.nestedClasses.mapNotNull { it.objectInstance as? InternetBankingResource }
    }

    object Bbl : InternetBankingResource(
            iconRes = R.drawable.payment_bbl,
            titleRes = R.string.payment_method_internet_banking_bbl_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.InternetBanking.Bbl
    )

    object Scb : InternetBankingResource(
            iconRes = R.drawable.payment_scb,
            titleRes = R.string.payment_method_internet_banking_scb_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.InternetBanking.Scb
    )

    object Bay : InternetBankingResource(
            iconRes = R.drawable.payment_bay,
            titleRes = R.string.payment_method_internet_banking_bay_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.InternetBanking.Bay
    )

    object Ktb : InternetBankingResource(
            iconRes = R.drawable.payment_ktb,
            titleRes = R.string.payment_method_internet_banking_ktb_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.InternetBanking.Ktb
    )
}

internal sealed class MobileBankingResource(
        @DrawableRes override val iconRes: Int,
        override val title: String? = null,
        @StringRes override val titleRes: Int? = null,
        @DrawableRes override val indicatorIconRes: Int,
        val sourceType: SourceType
) : OmiseListItem {

    companion object {
        val all: List<MobileBankingResource>
            get() = MobileBankingResource::class.nestedClasses.mapNotNull { it.objectInstance as? MobileBankingResource }
    }

    object Scb : MobileBankingResource(
            iconRes = R.drawable.payment_scb,
            titleRes = R.string.payment_method_mobile_banking_scb_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.MobileBanking.Scb
    )
}

internal class FpxResource(
        @DrawableRes override val iconRes: Int,
        override val title: String? = null,
        @DrawableRes override val indicatorIconRes: Int = R.drawable.ic_redirect,
        override val enabled: Boolean? = false,
        val bankCode: String? = null,
) : OmiseListItem {

    companion object {
        val all: List<FpxResource>
            get() = FpxResource::class.nestedClasses.mapNotNull { it.objectInstance as? FpxResource }

        fun getBankImageFromCode(code: String?) : Int {
            return when (code) {
                "affin" -> R.drawable.payment_affin
                "alliance" -> R.drawable.payment_alliance
                "agro" -> R.drawable.payment_agro
                "ambank" -> R.drawable.payment_ambank
                "islam" -> R.drawable.payment_islam
                "muamalat" -> R.drawable.payment_muamalat
                "rakyat" -> R.drawable.payment_rakyat
                "bsn" -> R.drawable.payment_bsn
                "cimb" -> R.drawable.payment_cimb
                "hongleong" -> R.drawable.payment_hongleong
                "hsbc" -> R.drawable.payment_hsbc
                "kfh" -> R.drawable.payment_kfh
                "maybank2e" -> R.drawable.payment_maybank
                "maybank2u" -> R.drawable.payment_maybank
                "ocbc" -> R.drawable.payment_ocbc
                "public" -> R.drawable.payment_publicbank
                "rhb" -> R.drawable.payment_rhb
                "sc" -> R.drawable.payment_sc
                "uob" -> R.drawable.payment_uob
                else -> R.drawable.payment_unknown
            }
        }
    }
}
