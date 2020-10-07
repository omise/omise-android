package co.omise.android.example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.omise.android.models.ChargeStatus
import co.omise.android.models.Token
import co.omise.android.ui.OmiseActivity
import kotlinx.android.synthetic.main.activity_payment_result.complete_button
import kotlinx.android.synthetic.main.activity_payment_result.result_message_text
import kotlinx.android.synthetic.main.activity_payment_result.result_moji_text

class PaymentResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_result)
        if (intent.hasExtra(OmiseActivity.EXTRA_TOKEN_OBJECT)) {
            val token = intent.getParcelableExtra<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
            when (token.chargeStatus) {
                ChargeStatus.Successful -> {
                    result_moji_text.text = "🎉"
                    result_message_text.text = "Your order is complete!"
                }
                else -> {
                    result_moji_text.text = "😅"
                    result_message_text.text = "Your order has failed with status ${token.chargeStatus.value}!"
                }
            }
        } else {
            result_moji_text.text = "😞"
            result_message_text.text = "Your order has failed!"
        }

        complete_button.setOnClickListener {
            Intent(this, CheckoutActivity::class.java).run {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(this)
            }
        }
    }
}