package co.omise.android.example

import android.content.Context
import androidx.preference.PreferenceManager
import co.omise.android.models.Bank
import co.omise.android.models.Capability
import co.omise.android.models.SourceType
import co.omise.android.models.TokenizationMethod


object PaymentSetting {
    @JvmStatic
    fun getPaymentMethodPreferences(context: Context): Map<String, Boolean> =
            listOf(
                    R.string.payment_preference_zero_interest_installments_key,
                    R.string.payment_preference_credit_card_key,
                    R.string.payment_preference_internet_banking_bay_key,
                    R.string.payment_preference_internet_banking_bbl_key,
                    R.string.payment_preference_mobile_banking_bay_key,
                    R.string.payment_preference_mobile_banking_bbl_key,
                    R.string.payment_preference_mobile_banking_kbank_key,
                    R.string.payment_preference_mobile_banking_ktb_key,
                    R.string.payment_preference_mobile_banking_scb_key,
                    R.string.payment_preference_installment_bay_key,
                    R.string.payment_preference_installment_first_choice_key,
                    R.string.payment_preference_installment_bbl_key,
                    R.string.payment_preference_installment_mbb_key,
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
                    R.string.payment_preference_ocbc_pao_key,
                    R.string.payment_preference_ocbc_digital_key,
                    R.string.payment_preference_paynow_key,
                    R.string.payment_preference_promptpay_key,
                    R.string.payment_preference_points_citi_key,
                    R.string.payment_preference_rabbit_linepay_key,
                    R.string.payment_preference_touch_n_go_key,
                    R.string.payment_preference_truemoney_key,
                    R.string.payment_preference_truemoney_jumpapp_key,
                    R.string.payment_preference_googlepay_key,
                    R.string.payment_preference_boost_key,
                    R.string.payment_preference_shopeepay_key,
                    R.string.payment_preference_duitnow_obw_key,
                    R.string.payment_preference_duitnow_qr_key,
                    R.string.payment_preference_maybank_qr_key,
                    R.string.payment_preference_grabpay_key,
                    R.string.payment_preference_paypay_key,
                    R.string.payment_preference_atome_key,
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
                        context.getString(R.string.payment_preference_internet_banking_bbl_key) -> SourceType.InternetBanking.Bbl
                        context.getString(R.string.payment_preference_alipay_key) -> SourceType.Alipay
                        context.getString(R.string.payment_preference_bill_payment_tesco_lotus_key) -> SourceType.BillPaymentTescoLotus
                        context.getString(R.string.payment_preference_mobile_banking_bay_key) -> SourceType.MobileBanking.Bay
                        context.getString(R.string.payment_preference_mobile_banking_bbl_key) -> SourceType.MobileBanking.Bbl
                        context.getString(R.string.payment_preference_mobile_banking_kbank_key) -> SourceType.MobileBanking.KBank
                        context.getString(R.string.payment_preference_mobile_banking_ktb_key) -> SourceType.MobileBanking.KTB
                        context.getString(R.string.payment_preference_ocbc_pao_key) -> SourceType.OcbcPao
                        context.getString(R.string.payment_preference_ocbc_digital_key) -> SourceType.OcbcDigital
                        context.getString(R.string.payment_preference_mobile_banking_scb_key) -> SourceType.MobileBanking.Scb
                        context.getString(R.string.payment_preference_installment_bay_key) -> SourceType.Installment.Bay
                        context.getString(R.string.payment_preference_installment_first_choice_key) -> SourceType.Installment.FirstChoice
                        context.getString(R.string.payment_preference_installment_bbl_key) -> SourceType.Installment.Bbl
                        context.getString(R.string.payment_preference_installment_mbb_key) -> SourceType.Installment.Mbb
                        context.getString(R.string.payment_preference_installment_ktc_key) -> SourceType.Installment.Ktc
                        context.getString(R.string.payment_preference_installment_kbank_key) -> SourceType.Installment.KBank
                        context.getString(R.string.payment_preference_installment_scb_key) -> SourceType.Installment.Scb
                        context.getString(R.string.payment_preference_installment_citi_key) -> SourceType.Installment.Citi
                        context.getString(R.string.payment_preference_installment_ttb_key) -> SourceType.Installment.Ttb
                        context.getString(R.string.payment_preference_installment_uob_key) -> SourceType.Installment.Uob
                        context.getString(R.string.payment_preference_econtext_key) -> SourceType.Econtext
                        context.getString(R.string.payment_preference_fpx_key) -> SourceType.Fpx()
                        context.getString(R.string.payment_preference_paynow_key) -> SourceType.PayNow
                        context.getString(R.string.payment_preference_promptpay_key) -> SourceType.PromptPay
                        context.getString(R.string.payment_preference_points_citi_key) -> SourceType.PointsCiti
                        context.getString(R.string.payment_preference_truemoney_key) -> SourceType.TrueMoney
                        context.getString(R.string.payment_preference_truemoney_jumpapp_key) -> SourceType.TrueMoneyJumpApp
                        context.getString(R.string.payment_preference_alipay_hk_key) -> SourceType.AlipayHk
                        context.getString(R.string.payment_preference_alipay_cn_key) -> SourceType.AlipayCn
                        context.getString(R.string.payment_preference_dana_key) -> SourceType.Dana
                        context.getString(R.string.payment_preference_gcash_key) -> SourceType.Gcash
                        context.getString(R.string.payment_preference_kakaopay_key) -> SourceType.Kakaopay
                        context.getString(R.string.payment_preference_touch_n_go_key) -> SourceType.TouchNGo()
                        context.getString(R.string.payment_preference_rabbit_linepay_key) -> SourceType.RabbitLinePay
                        context.getString(R.string.payment_preference_boost_key) -> SourceType.Boost
                        context.getString(R.string.payment_preference_shopeepay_key) -> SourceType.ShopeePay
                        context.getString(R.string.payment_preference_duitnow_obw_key) -> SourceType.DuitNowOBW
                        context.getString(R.string.payment_preference_duitnow_qr_key) -> SourceType.DuitNowQR
                        context.getString(R.string.payment_preference_maybank_qr_key) -> SourceType.MaybankQR
                        context.getString(R.string.payment_preference_grabpay_key) -> SourceType.GrabPay()
                        context.getString(R.string.payment_preference_paypay_key) -> SourceType.PayPay
                        context.getString(R.string.payment_preference_atome_key) -> SourceType.Atome
                        else -> null
                    }
                }
                .filterNotNull()

        val tokenizationMethods = paymentMethodPreferences
                .filter { it.value }
                .toMap()
                .mapNotNull {
                    when (it.key) {
                        context.getString(R.string.payment_preference_googlepay_key) -> TokenizationMethod.GooglePay
                        else -> null
                    }
                }

        val allowCreditCardMethod = paymentMethodPreferences[context.getString(R.string.payment_preference_credit_card_key)]
                ?: false

        val zeroInterestInstallments = paymentMethodPreferences[context.getString(R.string.payment_preference_zero_interest_installments_key)]
                ?: false

        val fpx = sourceTypes.find { it.name == "fpx" }
        if (fpx != null) {
            fpx as SourceType.Fpx
            fpx.banks = listOf(
                Bank("Affin Bank", "affin", true),
                Bank("Alliance Bank (Personal)", "alliance", true),
                Bank("Bank Islam", "islam", true),
                Bank("Standard Chartered", "sc", false)
            )
        }

        return Capability.create(allowCreditCardMethod, sourceTypes, tokenizationMethods, zeroInterestInstallments)
    }
}
