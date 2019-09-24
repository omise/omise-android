package co.omise.android.example

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat


class PaymentSettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_payment_setting, rootKey)
    }

}