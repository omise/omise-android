package co.omise.android.example

import android.content.Context
import androidx.preference.PreferenceManager
import co.omise.android.models.Bank
import co.omise.android.models.Capability
import co.omise.android.models.SourceType


object PaymentSetting {
    @JvmStatic
    fun getPaymentMethodPreferences(context: Context): Map<String, Boolean> =
            listOf(
                    R.string.payment_preference_zero_interest_installments_key,
                    R.string.payment_preference_credit_card_key,
                    R.string.payment_preference_internet_banking_bay_key,
                    R.string.payment_preference_internet_banking_ktb_key,
                    R.string.payment_preference_internet_banking_scb_key,
                    R.string.payment_preference_internet_banking_bbl_key,
                    R.string.payment_preference_mobile_banking_ocbc_pao_key,
                    R.string.payment_preference_mobile_banking_scb_key,
                    R.string.payment_preference_installment_bay_key,
                    R.string.payment_preference_installment_first_choice_key,
                    R.string.payment_preference_installment_bbl_key,
                    R.string.payment_preference_installment_ezypay_key,
                    R.string.payment_preference_installment_ktc_key,
                    R.string.payment_preference_installment_kbank_key,
                    R.string.payment_preference_installment_scb_key,
                    R.string.payment_preference_installment_citi_key,
                    R.string.payment_preference_installment_ttb_key,
                    R.string.payment_preference_installment_uob_key,
                    R.string.payment_preference_alipay_key,
                    R.string.payment_preference_alipay_cn_key,
                    R.string.payment_preference_alipay_hk_key,
                    R.string.payment_preference_bill_payment_tesco_lotus_key,
                    R.string.payment_preference_econtext_key,
                    R.string.payment_preference_dana_key,
                    R.string.payment_preference_fpx_key,
                    R.string.payment_preference_gcash_key,
                    R.string.payment_preference_kakaopay_key,
                    R.string.payment_preference_paynow_key,
                    R.string.payment_preference_promptpay_key,
                    R.string.payment_preference_points_citi_key,
                    R.string.payment_preference_touch_n_go_key,
                    R.string.payment_preference_truemoney_key
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
        val paymentMethodPreferences = getPaymentMethodPreferences(context)
        val sourceTypes = paymentMethodPreferences
                .filter { it.value }
                .toMap()
                .map {
                    when (it.key) {
                        context.getString(R.string.payment_preference_internet_banking_bay_key) -> SourceType.InternetBanking.Bay
                        context.getString(R.string.payment_preference_internet_banking_ktb_key) -> SourceType.InternetBanking.Ktb
                        context.getString(R.string.payment_preference_internet_banking_scb_key) -> SourceType.InternetBanking.Scb
                        context.getString(R.string.payment_preference_internet_banking_bbl_key) -> SourceType.InternetBanking.Bbl
                        context.getString(R.string.payment_preference_alipay_key) -> SourceType.Alipay
                        context.getString(R.string.payment_preference_bill_payment_tesco_lotus_key) -> SourceType.BillPaymentTescoLotus
                        context.getString(R.string.payment_preference_mobile_banking_ocbc_pao_key) -> SourceType.MobileBanking.OcbcPao
                        context.getString(R.string.payment_preference_mobile_banking_scb_key) -> SourceType.MobileBanking.Scb
                        context.getString(R.string.payment_preference_installment_bay_key) -> SourceType.Installment.Bay
                        context.getString(R.string.payment_preference_installment_first_choice_key) -> SourceType.Installment.FirstChoice
                        context.getString(R.string.payment_preference_installment_bbl_key) -> SourceType.Installment.Bbl
                        context.getString(R.string.payment_preference_installment_ezypay_key) -> SourceType.Installment.Ezypay
                        context.getString(R.string.payment_preference_installment_ktc_key) -> SourceType.Installment.Ktc
                        context.getString(R.string.payment_preference_installment_kbank_key) -> SourceType.Installment.KBank
                        context.getString(R.string.payment_preference_installment_scb_key) -> SourceType.Installment.Scb
                        context.getString(R.string.payment_preference_installment_citi_key) -> SourceType.Installment.Citi
                        context.getString(R.string.payment_preference_installment_ttb_key) -> SourceType.Installment.Ttb
                        context.getString(R.string.payment_preference_installment_uob_key) -> SourceType.Installment.Uob
                        context.getString(R.string.payment_preference_econtext_key) -> SourceType.Econtext
                        context.getString(R.string.payment_preference_fpx_key) -> SourceType.Fpx
                        context.getString(R.string.payment_preference_paynow_key) -> SourceType.PayNow
                        context.getString(R.string.payment_preference_promptpay_key) -> SourceType.PromptPay
                        context.getString(R.string.payment_preference_points_citi_key) -> SourceType.PointsCiti
                        context.getString(R.string.payment_preference_truemoney_key) -> SourceType.TrueMoney
                        context.getString(R.string.payment_preference_alipay_hk_key) -> SourceType.AlipayHk
                        context.getString(R.string.payment_preference_alipay_cn_key) -> SourceType.AlipayCn
                        context.getString(R.string.payment_preference_dana_key) -> SourceType.Dana
                        context.getString(R.string.payment_preference_gcash_key) -> SourceType.Gcash
                        context.getString(R.string.payment_preference_kakaopay_key) -> SourceType.Kakaopay
                        context.getString(R.string.payment_preference_touch_n_go_key) -> SourceType.TouchNGo
                        else -> null
                    }
                }
                .filterNotNull()

        val allowCreditCardMethod = paymentMethodPreferences[context.getString(R.string.payment_preference_credit_card_key)]
                ?: false

        val zeroInterestInstallments = paymentMethodPreferences[context.getString(R.string.payment_preference_zero_interest_installments_key)]
                ?: false

        val fpx = sourceTypes.find { it.name == "fpx" } as SourceType.Fpx
        fpx.banks = listOf(
                Bank("Affin Bank", "affin", true),
                Bank("Alliance Bank (Personal)", "alliance", true),
                Bank("Bank Islam", "islam", true),
                Bank("Standard Chartered", "sc", false)
        )

        return Capability.create(allowCreditCardMethod, sourceTypes, zeroInterestInstallments)
    }
}
