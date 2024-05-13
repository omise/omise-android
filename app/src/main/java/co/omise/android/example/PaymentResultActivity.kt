package co.omise.android.example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

/**
 * The example activity to receive the result of the payment.
 */
class PaymentResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_result)

        val resultText = findViewById<TextView>(R.id.payment_result_text)

        intent.data?.getQueryParameter("result")?.let { result ->
            resultText.text = "result: $result"
        } ?: run {
            resultText.text = "No result found"
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Depending on your application status, handle the result from the payment app as needed
    }
}
