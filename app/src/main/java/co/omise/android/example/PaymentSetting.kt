package co.omise.android.example

import android.content.Context
import androidx.preference.PreferenceManager
import co.omise.android.models.SourceType


object PaymentSetting {
    @JvmStatic
    fun getPaymentMethodSettingKeysWithSourceTypes(context: Context): List<Pair<String, SourceType>> =
            listOf(
                    Pair(context.getString(R.string.payment_preference_internet_banking_bay_key), SourceType.InternetBanking.Bay),
                    Pair(context.getString(R.string.payment_preference_internet_banking_ktb_key), SourceType.InternetBanking.Ktb),
                    Pair(context.getString(R.string.payment_preference_internet_banking_scb_key), SourceType.InternetBanking.Scb),
                    Pair(context.getString(R.string.payment_preference_alipay_key), SourceType.Alipay),
                    Pair(context.getString(R.string.payment_preference_bill_payment_tesco_lotus_key), SourceType.BillPaymentTescoLotus),
                    Pair(context.getString(R.string.payment_preference_installment_bay_key), SourceType.Installment.Bay),
                    Pair(context.getString(R.string.payment_preference_installment_first_choice_key), SourceType.Installment.FirstChoice),
                    Pair(context.getString(R.string.payment_preference_installment_bbl_key), SourceType.Installment.Bbl),
                    Pair(context.getString(R.string.payment_preference_installment_ktc_key), SourceType.Installment.Ktc),
                    Pair(context.getString(R.string.payment_preference_installment_kbank_key), SourceType.Installment.KBank),
                    Pair(context.getString(R.string.payment_preference_econtext_key), SourceType.Econtext)
            )

    @JvmStatic
    fun getAllowedSourceTypesFromSharedPreferences(context: Context): List<SourceType> =
            getPaymentMethodSettingKeysWithSourceTypes(context)
                    .filter { PreferenceManager.getDefaultSharedPreferences(context).getBoolean(it.first, false) }
                    .map { it.second }
}
