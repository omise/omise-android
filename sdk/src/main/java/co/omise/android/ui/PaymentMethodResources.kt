package co.omise.android.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import co.omise.android.R
import co.omise.android.models.BackendType
import co.omise.android.models.Capability
import co.omise.android.models.SourceType
import co.omise.android.models.backendType

val Capability.allowedPaymentChooserItems: List<PaymentChooserItem>
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

sealed class PaymentChooserItem(
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
