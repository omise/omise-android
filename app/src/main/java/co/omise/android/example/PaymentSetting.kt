package co.omise.android.example

import android.content.Context
import androidx.preference.PreferenceManager
import co.omise.android.models.Capability
import co.omise.android.models.PaymentMethod
import co.omise.android.models.SourceType


object PaymentSetting {
    @JvmStatic
    fun getPaymentMethodSettingKeysWithSourceTypes(context: Context): List<Pair<String, PaymentMethod>> =
            listOf(
                    Pair(context.getString(R.string.payment_preference_credit_card_key), PaymentMethod.cardMethod()),
                    Pair(context.getString(R.string.payment_preference_internet_banking_bay_key), PaymentMethod.fromSourceType(SourceType.InternetBanking.Bay)),
                    Pair(context.getString(R.string.payment_preference_internet_banking_ktb_key), PaymentMethod.fromSourceType(SourceType.InternetBanking.Ktb)),
                    Pair(context.getString(R.string.payment_preference_internet_banking_scb_key), PaymentMethod.fromSourceType(SourceType.InternetBanking.Scb)),
                    Pair(context.getString(R.string.payment_preference_internet_banking_bbl_key), PaymentMethod.fromSourceType(SourceType.InternetBanking.Bbl)),
                    Pair(context.getString(R.string.payment_preference_alipay_key), PaymentMethod.fromSourceType(SourceType.Alipay)),
                    Pair(context.getString(R.string.payment_preference_bill_payment_tesco_lotus_key), PaymentMethod.fromSourceType(SourceType.BillPaymentTescoLotus)),
                    Pair(context.getString(R.string.payment_preference_installment_bay_key), PaymentMethod.fromSourceType(SourceType.Installment.Bay)),
                    Pair(context.getString(R.string.payment_preference_installment_first_choice_key), PaymentMethod.fromSourceType(SourceType.Installment.FirstChoice)),
                    Pair(context.getString(R.string.payment_preference_installment_bbl_key), PaymentMethod.fromSourceType(SourceType.Installment.Bbl)),
                    Pair(context.getString(R.string.payment_preference_installment_ktc_key), PaymentMethod.fromSourceType(SourceType.Installment.Ktc)),
                    Pair(context.getString(R.string.payment_preference_installment_kbank_key), PaymentMethod.fromSourceType(SourceType.Installment.KBank)),
                    Pair(context.getString(R.string.payment_preference_econtext_key), PaymentMethod.fromSourceType(SourceType.Econtext))
            )

    @JvmStatic
    fun isUsedSpecificsPaymentMethods(context: Context): Boolean =
            PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(context.getString(R.string.payment_preference_is_use_specifics_payment_methods_key), false)

    @JvmStatic
    fun getCapabilityFromSharedPreferences(context: Context): Capability =
            Capability(
                    paymentMethods = getPaymentMethodSettingKeysWithSourceTypes(context)
                    .filter { PreferenceManager.getDefaultSharedPreferences(context).getBoolean(it.first, false) }
                    .map { it.second }
            )
}

fun createCapability(): Capability =
Capability.builder(allowCreditCard = true)
        .sourceTypes(SourceType.InternetBanking.Bay,SourceType.InternetBanking.Ktb)
        .build()

