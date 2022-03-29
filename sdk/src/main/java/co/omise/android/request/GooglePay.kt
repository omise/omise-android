package co.omise.android.request

import android.app.Activity
import co.omise.android.models.Amount
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.jvm.Throws

class GooglePay(
        private val pKey: String,
        private val cardNetworks: ArrayList<String>,
        private val price: Long,
        private val currencyCode: String,
        merchantId: String,
        private val requestBillingAddress: Boolean = false,
        private val requestPhoneNumber: Boolean = false
) {
    private val gateway = "omise"

    /**
     * Create a Google Pay API base request object with properties used in all requests.
     *
     * @return Google Pay API base request object.
     * @throws JSONException
     */
    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }

    /**
     *
     * The Google Pay API response will return an encrypted payment method capable of being charged
     * by a supported gateway after payer authorization.
     *
     *
     * @return Payment data tokenization for the CARD payment method.
     * @throws JSONException
     * @see [PaymentMethodTokenizationSpecification](https://developers.google.com/pay/api/android/reference/object.PaymentMethodTokenizationSpecification)
     */
    private fun gatewayTokenizationSpecification(): JSONObject {
        return JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put("parameters", JSONObject(mapOf(
                    "gateway" to gateway,
                    "gatewayMerchantId" to pKey)))
        }
    }

    /**
     * Card networks supported by your app and your gateway.
     *
     *
     * @return Allowed card networks
     * @see [CardParameters](https://developers.google.com/pay/api/android/reference/object.CardParameters)
     */
    private fun allowedCardNetworks(): JSONArray {
        val networksMapping= hashMapOf(
            "American Express" to "AMEX",
            "JCB" to "JCB",
            "MasterCard" to "MASTERCARD",
            "Visa" to "VISA"
        )

        var newList = arrayListOf<String>()
        if (this.cardNetworks.isNotEmpty())
            for(network in this.cardNetworks) {
                if (networksMapping[network] != null)
                    newList.add(networksMapping[network].toString())
            }
        else
            // If merchant doesn't decide to use capabilities
            newList = arrayListOf("AMEX", "JCB", "MASTERCARD", "VISA")

        return JSONArray(newList)
    }

    /**
     * Card authentication methods supported by your app and your gateway.
     * Mapped from Omise's capability list to GPay accepted format.
     *
     *
     * @return Allowed card authentication methods.
     * @see [CardParameters](https://developers.google.com/pay/api/android/reference/object.CardParameters)
     */
    private val allowedCardAuthMethods = JSONArray(listOf(
            "PAN_ONLY"))

    /**
     * Describe your app's support for the CARD payment method.
     *
     *
     * The provided properties are applicable to both an IsReadyToPayRequest and a
     * PaymentDataRequest.
     *
     * @return A CARD PaymentMethod object describing accepted cards.
     * @throws JSONException
     * @see [PaymentMethod](https://developers.google.com/pay/api/android/reference/object.PaymentMethod)
     */
    private fun baseCardPaymentMethod(): JSONObject {
        return JSONObject().apply {

            val parameters = JSONObject().apply {
                put("allowedAuthMethods", allowedCardAuthMethods)
                put("allowedCardNetworks", allowedCardNetworks())
                put("billingAddressRequired", requestBillingAddress)
                put("billingAddressParameters", JSONObject().apply {
                    put("format", "FULL")
                    put("phoneNumberRequired", requestPhoneNumber)
                })
            }

            put("type", "CARD")
            put("parameters", parameters)
        }
    }

    /**
     * Describe the expected returned payment data for the CARD payment method
     *
     * @return A CARD PaymentMethod describing accepted cards and optional fields.
     * @throws JSONException
     * @see [PaymentMethod](https://developers.google.com/pay/api/android/reference/object.PaymentMethod)
     */
    private fun cardPaymentMethod(): JSONObject {
        val cardPaymentMethod = baseCardPaymentMethod()
        cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification())

        return cardPaymentMethod
    }

    /**
     * Provide Google Pay API with a payment amount, currency, and amount status.
     *
     * @return information about the requested payment.
     * @throws JSONException
     * @see [TransactionInfo](https://developers.google.com/pay/api/android/reference/object.TransactionInfo)
     */
    @Throws(JSONException::class)
    private fun getTransactionInfo(price: Long, currencyCode: String): JSONObject {
        val priceUnits = Amount(price, currencyCode).toString(2)

        return JSONObject().apply {
            put("totalPrice", priceUnits)
            put("totalPriceStatus", "FINAL")
            put("currencyCode", currencyCode.toUpperCase())
        }
    }

    /**
     * An object describing accepted forms of payment by your app, used to determine a viewer's
     * readiness to pay.
     *
     * @return API version and payment methods supported by the app.
     * @see [IsReadyToPayRequest](https://developers.google.com/pay/api/android/reference/object.IsReadyToPayRequest)
     */
    fun isReadyToPayRequest(): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
            }

        } catch (e: JSONException) {
            null
        }
    }

    /**
     * Information about the merchant requesting payment information
     *
     * @return Information about the merchant.
     * @throws JSONException
     * @see [MerchantInfo](https://developers.google.com/pay/api/android/reference/object.MerchantInfo)
     */
    private val merchantInfo: JSONObject =
            JSONObject().put("merchantId", merchantId)

    /**
     * Creates an instance of [PaymentsClient] for use in an [Activity] using the
     * environment and theme set.
     *
     * @param activity is the caller's activity.
     */
    fun createPaymentsClient(activity: Activity): PaymentsClient {
        var env = WalletConstants.ENVIRONMENT_PRODUCTION
        if (isTestMode())
            env = WalletConstants.ENVIRONMENT_TEST

        val walletOptions = Wallet.WalletOptions.Builder()
                .setEnvironment(env)
                .build()

        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    /**
     * An object describing information requested in a Google Pay payment sheet
     *
     * @return Payment data expected by your app.
     * @see [PaymentDataRequest](https://developers.google.com/pay/api/android/reference/object.PaymentDataRequest)
     */
    fun getPaymentDataRequest(): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
                put("transactionInfo", getTransactionInfo(price, currencyCode))
                put("merchantInfo", merchantInfo)
            }
        } catch (e: JSONException) {
            null
        }
    }

    private fun isTestMode(): Boolean {
        return pKey.startsWith("pkey_test_")
    }
}

