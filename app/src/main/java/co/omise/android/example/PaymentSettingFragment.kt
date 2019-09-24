package co.omise.android.example

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


class PaymentSettingFragment : PreferenceFragmentCompat() {

    private val paymentMethodKeys: List<String> by lazy {
        listOf(
                "internet_banking_bay",
                "internet_banking_ktb",
                "internet_banking_scb",
                "internet_banking_bbl",
                "alipay",
                "bill_payment_tesco_lotus",
                "installment_bay",
                "installment_first_choice",
                "installment_bbl",
                "installment_ktc",
                "installment_kbank",
                "econtext"
        )
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_payment_setting, rootKey)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        preferenceScreen
                .findPreference<CheckBoxPreference>("is_use_specifics_payment_methods")?.apply {
                    enablePaymentMethodPreferences()
                }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "is_use_capability_api" -> preferenceScreen
                    .findPreference<CheckBoxPreference>("is_use_specifics_payment_methods")?.apply {
                        isChecked = !isChecked
                        enablePaymentMethodPreferences()
                    }
            "is_use_specifics_payment_methods" -> {
                preferenceScreen
                        .findPreference<CheckBoxPreference>("is_use_capability_api")?.apply {
                            isChecked = !isChecked
                            enablePaymentMethodPreferences()
                        }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun enablePaymentMethodPreferences() {
        val isUsedSpecificsPaymentMethod = preferenceScreen
                .findPreference<CheckBoxPreference>("is_use_specifics_payment_methods")?.isChecked
                ?: false

        paymentMethodKeys.forEach {
            preferenceScreen.findPreference<CheckBoxPreference>(it)?.apply {
                this.isEnabled = isUsedSpecificsPaymentMethod
            }
        }
    }
}
