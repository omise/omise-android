package co.omise.android.example

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.ui.AuthorizingPaymentActivity
import co.omise.android.ui.OmiseActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class PaymentProcessingActivity : AppCompatActivity() {
    private val testUrl = "https://587a438ff857.ngrok.io/charge/create"
    private val authorizingRequestCode = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_processing)
        supportActionBar?.title = "Payment"

        val tokenID = intent.getStringExtra(OmiseActivity.EXTRA_TOKEN)
        val amount = intent.getLongExtra(OmiseActivity.EXTRA_AMOUNT, 0)
        Log.d("PaymentProcessing", tokenID)

        val JSON = MediaType.get("application/json; charset=utf-8");
        val client = OkHttpClient()

        val param = """
            {
               "description":"test",
               "amount":$amount,
               "tokenID": "$tokenID",
               "orderID": "1"
            }
        """.trimIndent()
        val body = RequestBody.create(JSON, param);
        val request = Request.Builder()
                .url(testUrl)
                .post(body)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val json = response.body()?.string()
                Log.d("PaymentProcessing", json)

                val jsonObject = JSONObject(json)

                Intent(this@PaymentProcessingActivity, AuthorizingPaymentActivity::class.java).run {
                    putExtra(OmiseActivity.EXTRA_PKEY, CheckoutActivity.PUBLIC_KEY)
                    putExtra(OmiseActivity.EXTRA_TOKEN, tokenID)
                    putExtra(AuthorizingPaymentURLVerifier.EXTRA_AUTHORIZED_URLSTRING, jsonObject.getString("authorize_uri"))
                    putExtra(AuthorizingPaymentURLVerifier.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS,
                            arrayOf(jsonObject.getString("return_uri")))
                    startActivityForResult(this, authorizingRequestCode)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // TODO: Send failure result
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED) {
            // TODO: Send failure result
            return
        }

        if (requestCode == authorizingRequestCode && resultCode == Activity.RESULT_OK) {
            TODO("Send successful result")
        }
    }
}
