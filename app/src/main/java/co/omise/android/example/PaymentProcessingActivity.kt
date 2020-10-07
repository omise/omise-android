package co.omise.android.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import co.omise.android.ui.OmiseActivity

class PaymentProcessingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_processing)
        supportActionBar?.title = "Payment"

        val tokenID = intent.getStringExtra(OmiseActivity.EXTRA_TOKEN)
        Log.d("PaymentProcessing", tokenID)
    }
}
