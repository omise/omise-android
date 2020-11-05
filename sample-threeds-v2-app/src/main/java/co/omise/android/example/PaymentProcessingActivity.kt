package co.omise.android.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.config.AuthorizingPaymentConfig
import co.omise.android.config.ThreeDSConfig
import co.omise.android.config.UiCustomization
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
    private val testUrl = "https://b43e55ee242a.ngrok.io/charge/create"
    private val authorizingRequestCode = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_processing)
        supportActionBar?.title = "Payment"

        val tokenID = intent.getStringExtra(OmiseActivity.EXTRA_TOKEN)
        val amount = intent.getLongExtra(OmiseActivity.EXTRA_AMOUNT, 0)

        val JSON = MediaType.get("application/json; charset=utf-8");
        val client = OkHttpClient()

        val is3DsV1 = amount.toString().let { it[it.length - 2] == '2' }
        val param = """
            {
               "description":"test",
               "amount":$amount,
               "tokenID": "$tokenID",
               "orderID": "1",
               "capture": $is3DsV1
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
                val jsonObject = JSONObject(json)

                initializeAuthoringPaymentConfig()

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
                Intent(this@PaymentProcessingActivity, PaymentResultActivity::class.java).run {
                    startActivity(this)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                Intent(this, PaymentResultActivity::class.java).run {
                    data?.let { putExtras(it) }
                    startActivity(this)
                }
            }
            Activity.RESULT_CANCELED -> {
                Intent(this, PaymentResultActivity::class.java).run {
                    putExtra(OmiseActivity.EXTRA_ERROR, data?.getStringExtra(OmiseActivity.EXTRA_ERROR))
                    startActivity(this)
                }
            }
        }
    }

    private fun initializeAuthoringPaymentConfig() {
        val uiCustomization = UiCustomization.Builder()
                .labelCustomization(UiCustomization.LabelCustomization.Builder()
                        .textFontName("roboto")
                        .textFontColor("#000000")
                        .textFontSize(16)
                        .headingTextColor("roboto")
                        .headingTextFontName("#000000")
                        .headingTextFontSize(28)
                        .build())
                .textBoxCustomization(UiCustomization.TextBoxCustomization.Builder()
                        .textFontName("roboto")
                        .textFontColor("#000000")
                        .textFontSize(16)
                        .borderWidth(2)
                        .cornerRadius(8)
                        .borderColor("#FF0000")
                        .build())
//                .toolbarCustomization()
//                .buttonCustomization()
                .build()

        val threeDSConfig = ThreeDSConfig.Builder()
                .uiCustomization(uiCustomization)
                .timeout(5)
                .build()
        val authPaymentConfig = AuthorizingPaymentConfig.Builder()
                .threeDSConfig(threeDSConfig)
                .build()
        AuthorizingPaymentConfig.initialize(authPaymentConfig)
    }
}
