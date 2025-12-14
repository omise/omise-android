package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.VisibleForTesting
import co.omise.android.R
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.extensions.getMessageFromResources
import co.omise.android.models.APIError
import co.omise.android.models.Token
import co.omise.android.models.TokenizationParam
import co.omise.android.request.GooglePay
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import kotlinx.android.synthetic.main.activity_google_pay.googlePayButton
import org.json.JSONException
import org.json.JSONObject
import java.io.IOError

class GooglePayActivity : OmiseActivity() {
    private lateinit var pKey: String
    private lateinit var googlePay: GooglePay

    @VisibleForTesting
    internal lateinit var paymentsClient: PaymentsClient

    private lateinit var cardNetworks: ArrayList<String>
    private var price: Long = 0
    private lateinit var currencyCode: String
    private lateinit var merchantId: String
    private var requestBillingAddress: Boolean = false
    private var requestPhoneNumber: Boolean = false

    @VisibleForTesting
    internal var client: Client? = null

    /**
     * Arbitrarily-picked constant integer you define to track a request for payment data activity.
     *
     * @value #LOAD_PAYMENT_DATA_REQUEST_CODE
     */
    @VisibleForTesting
    internal val loadPaymentDataRequestCode = 991

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_pay)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        initialize()

        setTitle(R.string.googlepay)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        googlePay =
            GooglePay(
                pKey,
                cardNetworks,
                price,
                currencyCode,
                merchantId,
                requestBillingAddress,
                requestPhoneNumber,
            )

        if (!::paymentsClient.isInitialized) {
            paymentsClient = googlePay.createPaymentsClient(this)
        }

        possiblyShowGooglePayButton()

        googlePayButton.setOnClickListener { requestPayment() }
    }

    private fun initialize() {
        pKey =
            requireNotNull(intent.getStringExtra(OmiseActivity.EXTRA_PKEY)) {
                "${OmiseActivity.Companion::EXTRA_PKEY.name} must not be null."
            }
        cardNetworks =
            requireNotNull(intent.getStringArrayListExtra(OmiseActivity.EXTRA_CARD_BRANDS)) {
                "${OmiseActivity.Companion::EXTRA_CARD_BRANDS.name} must not be null."
            }
        price =
            requireNotNull(intent.getLongExtra(OmiseActivity.EXTRA_AMOUNT, 0)) {
                "${OmiseActivity.Companion::EXTRA_AMOUNT.name} must not be null."
            }
        currencyCode =
            requireNotNull(intent.getStringExtra(OmiseActivity.EXTRA_CURRENCY)) {
                "${OmiseActivity.Companion::EXTRA_CURRENCY.name} must not be null."
            }
        merchantId =
            requireNotNull(intent.getStringExtra(OmiseActivity.EXTRA_GOOGLEPAY_MERCHANT_ID)) {
                "${OmiseActivity.Companion::EXTRA_GOOGLEPAY_MERCHANT_ID.name} must not be null."
            }
        requestBillingAddress =
            requireNotNull(intent.getBooleanExtra(OmiseActivity.EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS, false)) {
                "${OmiseActivity.Companion::EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS.name} must not be null."
            }
        requestPhoneNumber =
            requireNotNull(intent.getBooleanExtra(OmiseActivity.EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER, false)) {
                "${OmiseActivity.Companion::EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER.name} must not be null."
            }
    }

    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button.
     *
     * @see [](https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient.html.isReadyToPay
     ) */
    @VisibleForTesting
    internal fun possiblyShowGooglePayButton() {
        val isReadyToPayJson = googlePay.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let(::setGooglePayAvailable)
            } catch (exception: ApiException) {
                Log.w("isReadyToPay failed", exception)
                Toast.makeText(
                    this,
                    "Internal error occurred, please try a different payment method",
                    Toast.LENGTH_LONG,
                ).show()
                onBackPressedCallback.handleOnBackPressed()
            }
        }
    }

    /**
     * If isReadyToPay returned `true`, show the button and hide the "checking" text. Otherwise,
     * notify the user that Google Pay is not available. Please adjust to fit in with your current
     * user flow. You are not required to explicitly let the user know if isReadyToPay returns `false`.
     *
     * @param available isReadyToPay API response.
     */
    @VisibleForTesting
    internal fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            googlePayButton.visibility = View.VISIBLE
        } else {
            Toast.makeText(
                this,
                "Unfortunately, Google Pay is not available on this device",
                Toast.LENGTH_LONG,
            ).show()
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    @VisibleForTesting
    internal fun requestPayment() {
        // Disables the button to prevent multiple clicks.
        googlePayButton.isClickable = false

        val paymentDataRequestJson = googlePay.getPaymentDataRequest()
        if (paymentDataRequestJson == null) {
            Log.e("RequestPayment", "Can't fetch payment data request")
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        AutoResolveHelper.resolveTask(
            paymentsClient.loadPaymentData(request),
            this,
            loadPaymentDataRequestCode,
        )
    }

    // can't move from deprecated onActivityResult due to https://github.com/Adyen/adyen-android/issues/771

    /**
     * Handle a resolved activity from the Google Pay payment sheet.
     *
     * @param requestCode Request code originally supplied to AutoResolveHelper in requestPayment().
     * @param resultCode Result code returned by the Google Pay API.
     * @param data Intent from the Google Pay API containing payment or error data.
     * @see [Getting a result
     * from an Activity](https://developer.android.com/training/basics/intents/result)
     */
    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            loadPaymentDataRequestCode -> {
                when (resultCode) {
                    RESULT_OK ->
                        data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                        }

                    RESULT_CANCELED -> {
                        onBackPressedCallback.handleOnBackPressed()
                    }

                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode)
                        }
                    }
                }

                // Re-enables the Google Pay payment button.
                googlePayButton.isClickable = true
            }
        }
    }

    /**
     * PaymentData response object contains the payment information.
     * We will call our tokens API here and add a listener to wait for its response.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see [Payment
     * Data](https://developers.google.com/pay/api/android/reference/object.PaymentData)
     */
    @VisibleForTesting
    internal fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson()
        var billingName = ""
        var billingCity = ""
        var billingCountry = ""
        var billingPostalCode = ""
        var billingState = ""
        var billingStreet1 = ""
        var billingStreet2 = ""
        var billingPhoneNumber = ""

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")

            val paymentToken =
                paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")

            if (requestBillingAddress) {
                val billingAddress =
                    paymentMethodData.getJSONObject("info")
                        .getJSONObject("billingAddress")

                billingName = billingAddress.getString("name")
                billingCity = billingAddress.getString("locality")
                billingCountry = billingAddress.getString("countryCode")
                billingPostalCode = billingAddress.getString("postalCode")
                billingState = billingAddress.getString("administrativeArea")
                billingStreet1 = billingAddress.getString("address1")
                billingStreet2 =
                    listOf(
                        billingAddress.getString("address2"),
                        billingAddress.getString("address3"),
                    ).filter { s -> s != "" }.joinToString(" ")

                if (requestPhoneNumber) {
                    billingPhoneNumber = billingAddress.getString("phoneNumber")
                }
            }

            val tokenParam =
                TokenizationParam(
                    method = "googlepay",
                    data = paymentToken,
                    billingName = billingName,
                    billingCity = billingCity,
                    billingCountry = billingCountry,
                    billingPostalCode = billingPostalCode,
                    billingState = billingState,
                    billingStreet1 = billingStreet1,
                    billingStreet2 = billingStreet2,
                    billingPhoneNumber = billingPhoneNumber,
                )

            googlePayButton.isClickable = false

            val request =
                Token.CreateTokenRequestBuilder(tokenization = tokenParam).build()

            val listener = CreateTokenRequestListener()
            try {
                val clientToUse = client ?: Client(pKey)
                clientToUse.send(request, listener)
            } catch (ex: Exception) {
                listener.onRequestFailed(ex)
            }
        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: $e")
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     * WalletConstants.ERROR_CODE_* constants.
     * @see [
     * Wallet Constants Library](https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants.constant-summary)
     */
    @VisibleForTesting
    internal fun handleError(statusCode: Int) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
    }

    @VisibleForTesting
    internal inner class CreateTokenRequestListener : RequestListener<Token> {
        override fun onRequestSucceed(model: Token) {
            val data = Intent()
            data.putExtra(OmiseActivity.EXTRA_TOKEN, model.id)
            data.putExtra(OmiseActivity.EXTRA_TOKEN_OBJECT, model)
            data.putExtra(OmiseActivity.EXTRA_CARD_OBJECT, model.card)

            setResult(Activity.RESULT_OK, data)
            finish()
        }

        override fun onRequestFailed(throwable: Throwable) {
            val message =
                when (throwable) {
                    is IOError -> getString(R.string.error_io, throwable.message)
                    is APIError -> throwable.getMessageFromResources(resources)
                    else -> getString(R.string.error_unknown, throwable.message)
                }

            Toast.makeText(
                baseContext,
                message,
                Toast.LENGTH_LONG,
            ).show()
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @VisibleForTesting
    internal val onBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
}
