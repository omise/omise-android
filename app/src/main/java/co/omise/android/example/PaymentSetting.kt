package co.omise.android.example

import android.content.Context
import androidx.preference.PreferenceManager
import co.omise.android.models.Capability
import co.omise.android.models.SourceType


object PaymentSetting {
    @JvmStatic
    fun getPaymentMethodPreferences(context: Context): Map<String, Boolean> =
            listOf(
                    R.string.payment_preference_credit_card_key,
                    R.string.payment_preference_internet_banking_bay_key,
                    R.string.payment_preference_internet_banking_ktb_key,
                    R.string.payment_preference_internet_banking_scb_key,
                    R.string.payment_preference_internet_banking_bbl_key,
                    R.string.payment_preference_alipay_key,
                    R.string.payment_preference_bill_payment_tesco_lotus_key,
                    R.string.payment_preference_installment_bay_key,
                    R.string.payment_preference_installment_first_choice_key,
                    R.string.payment_preference_installment_bbl_key,
                    R.string.payment_preference_installment_ktc_key,
                    R.string.payment_preference_installment_kbank_key,
                    R.string.payment_preference_econtext_key
            )
                    .map { context.getString(it) }
                    .map { Pair(it, PreferenceManager.getDefaultSharedPreferences(context).getBoolean(it, false)) }
                    .toMap()

    @JvmStatic
    fun isUsedSpecificsPaymentMethods(context: Context): Boolean =
            PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(context.getString(R.string.payment_preference_is_use_specifics_payment_methods_key), false)

    @JvmStatic
    fun createCapabilityFromPreferences(context: Context): Capability {
        val sourceTypes = getPaymentMethodPreferences(context)
                .filter { it.value }
                .toMap()
                .map {
                    when (it.key) {
                        context.getString(R.string.payment_preference_credit_card_key) -> SourceType.InternetBanking.Bay
                        context.getString(R.string.payment_preference_internet_banking_bay_key) -> SourceType.InternetBanking.Bay
                        context.getString(R.string.payment_preference_internet_banking_ktb_key) -> SourceType.InternetBanking.Ktb
                        context.getString(R.string.payment_preference_internet_banking_scb_key) -> SourceType.InternetBanking.Scb
                        context.getString(R.string.payment_preference_internet_banking_bbl_key) -> SourceType.InternetBanking.Bbl
                        context.getString(R.string.payment_preference_alipay_key) -> SourceType.Alipay
                        context.getString(R.string.payment_preference_bill_payment_tesco_lotus_key) -> SourceType.BillPaymentTescoLotus
                        context.getString(R.string.payment_preference_installment_bay_key) -> SourceType.Installment.Bay
                        context.getString(R.string.payment_preference_installment_first_choice_key) -> SourceType.Installment.FirstChoice
                        context.getString(R.string.payment_preference_installment_bbl_key) -> SourceType.Installment.Bbl
                        context.getString(R.string.payment_preference_installment_ktc_key) -> SourceType.Installment.Ktc
                        context.getString(R.string.payment_preference_installment_kbank_key) -> SourceType.Installment.KBank
                        context.getString(R.string.payment_preference_econtext_key) -> SourceType.Econtext
                        else -> null
                    }
                }
                .filterNotNull()

        val allowCreditCardMethod = getPaymentMethodPreferences(context)
                .get(context.getString(R.string.payment_preference_credit_card_key)) ?: false

        return Capability.create(allowCreditCardMethod, sourceTypes)
    }
}
