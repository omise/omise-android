package co.omise.android.example

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


class PaymentSettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_payment_setting, rootKey)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.setTitle(R.string.activity_payment_setting)

        enablePaymentMethodsSettingIfNeeded()
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            getString(R.string.payment_preference_is_use_capability_api_key) -> {
                checkedCheckBoxPreference(getString(R.string.payment_preference_is_use_specifics_payment_methods_key))
                enablePaymentMethodsSettingIfNeeded()
            }
            getString(R.string.payment_preference_is_use_specifics_payment_methods_key) -> {
                checkedCheckBoxPreference(getString(R.string.payment_preference_is_use_capability_api_key))
                enablePaymentMethodsSettingIfNeeded()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun enablePaymentMethodsSettingIfNeeded() {
        val isUsedSpecificsPaymentMethod = preferenceScreen
                .findPreference<CheckBoxPreference>(getString(R.string.payment_preference_is_use_specifics_payment_methods_key))?.isChecked
                ?: false

        PaymentSetting.getPaymentMethodPreferences(context!!).forEach {
            preferenceScreen.findPreference<CheckBoxPreference>(it.key)?.apply {
                this.isEnabled = isUsedSpecificsPaymentMethod
            }
        }
    }

    private fun checkedCheckBoxPreference(preferenceKey: String) {
        preferenceScreen
                .findPreference<CheckBoxPreference>(preferenceKey)
                ?.apply {
                    isChecked = !isChecked
                }
    }
}
