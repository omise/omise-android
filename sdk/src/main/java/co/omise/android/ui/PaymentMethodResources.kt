package co.omise.android.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.Capability
import co.omise.android.models.SourceType
import co.omise.android.models.backendType

internal val Capability.allowedPaymentChooserItems: List<PaymentChooserItem>
    get() {
        val items = mutableListOf<PaymentChooserItem>()
        this.paymentMethods
                .orEmpty()
                .forEach { paymentMethod ->
                    when (paymentMethod.backendType) {
                        BackendType.Token -> items.add(PaymentChooserItem.CreditCard)
                        is BackendType.Source -> when ((paymentMethod.backendType as BackendType.Source).sourceType) {
                            is SourceType.Installment -> items.add(PaymentChooserItem.Installments)
                            is SourceType.InternetBanking -> items.add(PaymentChooserItem.InternetBankings)
                            is SourceType.MobileBanking -> items.add(PaymentChooserItem.MobileBankings)
                            is SourceType.Econtext -> items.addAll(listOf(PaymentChooserItem.ConvenienceStore, PaymentChooserItem.PayEasy, PaymentChooserItem.Netbanking))
                            else -> PaymentChooserItem.all.find { it.sourceType == (paymentMethod.backendType as? BackendType.Source)?.sourceType }?.let { items.add(it) }
                        }
                    }
                }
        return items.distinct()
    }

internal val List<SourceType.Installment>.allowedInstallmentChooserItems: List<InstallmentChooserItem>
    get() = this.mapNotNull { sourceType -> InstallmentChooserItem.all.find { it.sourceType == sourceType } }

internal val List<SourceType.InternetBanking>.allowedInternetBankingChooserItems: List<InternetBankingChooserItem>
    get() = this.mapNotNull { sourceType -> InternetBankingChooserItem.all.find { it.sourceType == sourceType } }

internal val List<SourceType.MobileBanking>.allowedMobileBankingChooserItems: List<MobileBankingChooserItem>
    get() = this.mapNotNull { sourceType -> MobileBankingChooserItem.all.find { it.sourceType == sourceType } }

internal sealed class PaymentChooserItem(
        @DrawableRes override val iconRes: Int,
        @StringRes override val titleRes: Int?,
        @DrawableRes override val indicatorIconRes: Int,
        val isCreditCard: Boolean = false,
        val sourceType: SourceType? = null
) : OmiseListItem {
    object CreditCard : PaymentChooserItem(
            iconRes = R.drawable.payment_card,
            titleRes = R.string.payment_method_credit_card_title,
            indicatorIconRes = R.drawable.ic_next,
            isCreditCard = true
    )

    object Installments : PaymentChooserItem(
            iconRes = R.drawable.payment_installment,
            titleRes = R.string.payment_method_installments_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object InternetBankings : PaymentChooserItem(
            iconRes = R.drawable.payment_banking,
            titleRes = R.string.payment_method_internet_banking_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object MobileBankings : PaymentChooserItem(
            iconRes = R.drawable.payment_mobile,
            titleRes = R.string.payment_method_mobile_banking_title,
            indicatorIconRes = R.drawable.ic_next
    )

    object TescoLotus : PaymentChooserItem(
            iconRes = R.drawable.payment_tesco,
            titleRes = R.string.payment_method_tesco_lotus_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.BillPaymentTescoLotus
    )

    object ConvenienceStore : PaymentChooserItem(
            iconRes = R.drawable.payment_conbini,
            titleRes = R.string.payment_method_convenience_store_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Econtext
    )

    object PayEasy : PaymentChooserItem(
            iconRes = R.drawable.payment_payeasy,
            titleRes = R.string.payment_method_pay_easy_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Econtext
    )

    object Netbanking : PaymentChooserItem(
            iconRes = R.drawable.payment_netbank,
            titleRes = R.string.payment_method_netbank_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Econtext
    )

    object Alipay : PaymentChooserItem(
            iconRes = R.drawable.payment_alipay,
            titleRes = R.string.payment_method_alipay_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.Alipay
    )

    object PromptPay : PaymentChooserItem(
            iconRes = R.drawable.payment_promptpay,
            titleRes = R.string.payment_method_promptpay_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.PromptPay
    )

    object PayNow : PaymentChooserItem(
            iconRes = R.drawable.payment_paynow,
            titleRes = R.string.payment_method_paynow_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.PayNow
    )

    object PointsCiti : PaymentChooserItem(
            iconRes = R.drawable.payment_points_citi,
            titleRes = R.string.payment_method_points_citi_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.PointsCiti
    )

    object TrueMoney : PaymentChooserItem(
            iconRes = R.drawable.payment_truemoney,
            titleRes = R.string.payment_truemoney_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.TrueMoney
    )

    companion object {
        val all: List<PaymentChooserItem>
            get() = PaymentChooserItem::class.nestedClasses.mapNotNull { it.objectInstance as? PaymentChooserItem }
    }
}

internal sealed class InstallmentChooserItem(
        @DrawableRes override val iconRes: Int,
        override val title: String? = null,
        @StringRes override val titleRes: Int? = null,
        @DrawableRes override val indicatorIconRes: Int,
        val sourceType: SourceType
) : OmiseListItem {
    companion object {
        val all: List<InstallmentChooserItem>
            get() = InstallmentChooserItem::class.nestedClasses.mapNotNull { it.objectInstance as? InstallmentChooserItem }
    }

    object Bbl : InstallmentChooserItem(
            iconRes = R.drawable.payment_bbl,
            titleRes = R.string.payment_method_installment_bbl_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Installment.Bbl
    )

    object KBank : InstallmentChooserItem(
            iconRes = R.drawable.payment_kasikorn,
            titleRes = R.string.payment_method_installment_kasikorn_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Installment.KBank
    )

    object Bay : InstallmentChooserItem(
            iconRes = R.drawable.payment_bay,
            titleRes = R.string.payment_method_installment_bay_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Installment.Bay
    )

    object FirstChoice : InstallmentChooserItem(
            iconRes = R.drawable.payment_first_choice,
            titleRes = R.string.payment_method_installment_first_choice_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Installment.FirstChoice
    )

    object Ktc : InstallmentChooserItem(
            iconRes = R.drawable.payment_ktc,
            titleRes = R.string.payment_method_installment_ktc_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Installment.Ktc
    )

    object Scb : InstallmentChooserItem(
            iconRes = R.drawable.payment_scb,
            titleRes = R.string.payment_method_installment_scb_title,
            indicatorIconRes = R.drawable.ic_next,
            sourceType = SourceType.Installment.Scb
    )
}

internal data class InstallmentTermChooserItem(
        @DrawableRes override val iconRes: Int? = null,
        override val title: String,
        val installmentTerm: Int,
        @DrawableRes override val indicatorIconRes: Int = R.drawable.ic_redirect
) : OmiseListItem

internal sealed class InternetBankingChooserItem(
        @DrawableRes override val iconRes: Int,
        override val title: String? = null,
        @StringRes override val titleRes: Int? = null,
        @DrawableRes override val indicatorIconRes: Int,
        val sourceType: SourceType
) : OmiseListItem {

    companion object {
        val all: List<InternetBankingChooserItem>
            get() = InternetBankingChooserItem::class.nestedClasses.mapNotNull { it.objectInstance as? InternetBankingChooserItem }
    }

    object Bbl : InternetBankingChooserItem(
            iconRes = R.drawable.payment_bbl,
            titleRes = R.string.payment_method_internet_banking_bbl_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.InternetBanking.Bbl
    )

    object Scb : InternetBankingChooserItem(
            iconRes = R.drawable.payment_scb,
            titleRes = R.string.payment_method_internet_banking_scb_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.InternetBanking.Scb
    )

    object Bay : InternetBankingChooserItem(
            iconRes = R.drawable.payment_bay,
            titleRes = R.string.payment_method_internet_banking_bay_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.InternetBanking.Bay
    )

    object Ktb : InternetBankingChooserItem(
            iconRes = R.drawable.payment_ktb,
            titleRes = R.string.payment_method_internet_banking_ktb_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.InternetBanking.Ktb
    )
}

internal sealed class MobileBankingChooserItem(
        @DrawableRes override val iconRes: Int,
        override val title: String? = null,
        @StringRes override val titleRes: Int? = null,
        @DrawableRes override val indicatorIconRes: Int,
        val sourceType: SourceType
) : OmiseListItem {

    companion object {
        val all: List<MobileBankingChooserItem>
            get() = MobileBankingChooserItem::class.nestedClasses.mapNotNull { it.objectInstance as? MobileBankingChooserItem }
    }

    object Scb : MobileBankingChooserItem(
            iconRes = R.drawable.payment_scb,
            titleRes = R.string.payment_method_mobile_banking_scb_title,
            indicatorIconRes = R.drawable.ic_redirect,
            sourceType = SourceType.MobileBanking.Scb
    )
}
