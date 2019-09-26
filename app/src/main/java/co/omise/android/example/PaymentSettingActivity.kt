package co.omise.android.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PaymentSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_setting)

        supportFragmentManager.beginTransaction()
                .replace(R.id.content, PaymentSettingFragment())
                .commit()
    }
}
