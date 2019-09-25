package co.omise.android.example

import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import co.omise.android.models.SourceType


abstract class BaseActivity : AppCompatActivity() {

    protected val paymentsFromSharedPreferences: List<SourceType>
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

            return PaymentSettingFragment.paymentMethodKeys
                    .map { Pair(it, sharedPreferences.getBoolean(it, false)) }
                    .filter { it.second }
                    .map {
                        when (it.first) {
                            "internet_banking_bay" -> SourceType.InternetBanking.Bay
                            else -> SourceType.Installment.Unknown(it.first)
                        }
                    }
        }
}
