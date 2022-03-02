package co.omise.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.models.Capability
import co.omise.android.models.Googlepay
import co.omise.android.models.PaymentMethod
import co.omise.android.models.TokenizationMethod
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TokenizationMethodTest {

    @Test
    fun addTokenizationToPaymentMethodCapability() {
        val paymentMethodList: List<PaymentMethod> = (1..10).map {
            PaymentMethod(
                    "Method no: $it",
                    listOf("thb, usd, myr, sgd, jpy"),
                    listOf("VISA", "MASTER", "LASER"),
                    listOf(1, 2, 4, 6))
        }
        val tokenizationMethodList: List<String> = listOf(
                "googlepay",
                "kanpay"
            )
        val capability = Capability(
                mutableListOf("a", "b", "c", "d"),
                paymentMethodList as MutableList<PaymentMethod>?,
                tokenizationMethodList,
                false)

        capability.paymentMethods?.any{ it.name == "Method no: 1" }?.let { assertTrue(it) }
        capability.paymentMethods?.any{ it.name == "googlepay" }?.let { assertTrue(it) }
        capability.paymentMethods?.any{ it.name == "kanpay" }?.let { assertTrue(it) }
    }

    @Test
    fun canCreateTokenizationMethodFromName() {
        val gpayToken = TokenizationMethod.creator("googlepay")
        val card = TokenizationMethod.creator("card")

        assertEquals(TokenizationMethod.GooglePay, gpayToken)
        assertEquals(TokenizationMethod.Card, card)
    }


    @Test
    fun cannotCreateTokenizationMethodFromInvalidName() {
        val tokenizationMethod = TokenizationMethod.creator("aaa")

        assertEquals(TokenizationMethod.Unknown("aaa"), tokenizationMethod)
    }

    //
    // Google Pay Tests
    //

    @Test
    fun googlepay_isReadyToPayRequest_returnsCorrectValues() {
        val googlepay = Googlepay("pkey_123", arrayListOf("Visa"), 3000, "thb", "merchantId")
        val isReadyToPay = googlepay.isReadyToPayRequest()


        val expected = JSONObject(
            """
            {
              "apiVersion": 2,
              "apiVersionMinor": 0,
              "allowedPaymentMethods": [
                {
                  "type": "CARD",
                  "parameters": {
                    "allowedAuthMethods": [
                      "PAN_ONLY"
                    ],
                    "allowedCardNetworks": [
                      "VISA"
                    ]
                  }
                }
              ]
            }
            """
        )

        val mapper = ObjectMapper()
        assertEquals(mapper.readTree(expected.toString()), mapper.readTree(isReadyToPay.toString()))
    }


    @Test
    fun googlepay_useDefaultCardNetworksIfMissingFromCapabilities() {
        val googlepay = Googlepay("pkey_123", arrayListOf(), 3000, "thb", "merchantId")
        val isReadyToPay = googlepay.isReadyToPayRequest()


        val expected = JSONObject(
            """
            {
              "apiVersion": 2,
              "apiVersionMinor": 0,
              "allowedPaymentMethods": [
                {
                  "type": "CARD",
                  "parameters": {
                    "allowedAuthMethods": [
                      "PAN_ONLY"
                    ],
                    "allowedCardNetworks": [
                      "AMEX",
                      "JCB",
                      "MASTERCARD",
                      "VISA"
                    ]
                  }
                }
              ]
            }
            """
        )

        val mapper = ObjectMapper()
        assertEquals(mapper.readTree(expected.toString()), mapper.readTree(isReadyToPay.toString()))
    }


    @Test
    fun googlepay_getPaymentDataRequest_returnsCorrectValues() {
        val googlepay = Googlepay("pkey_123", arrayListOf("American Express", "MasterCard", "JCB"), 3000, "sgd", "merchantId")
        val getPaymentDataRequest = googlepay.getPaymentDataRequest()


        val expected = JSONObject(
            """
            {
              "apiVersion": 2,
              "apiVersionMinor": 0,
              "allowedPaymentMethods": [
                {
                  "type": "CARD",
                  "parameters": {
                    "allowedAuthMethods": [
                      "PAN_ONLY"
                    ],
                    "allowedCardNetworks": [
                      "AMEX",
                      "MASTERCARD",
                      "JCB"
                    ]
                  },
                  "tokenizationSpecification": {
                    "type": "PAYMENT_GATEWAY",
                    "parameters": {
                      "gateway": "omise",
                      "gatewayMerchantId": "pkey_123"
                    }
                  }
                }
              ],
              "transactionInfo": {
                "totalPrice": "30.00",
                "totalPriceStatus": "FINAL",
                "currencyCode": "SGD"
              },
              "merchantInfo": {
                "merchantId": "merchantId"
              }
            }
            """
        )

        val mapper = ObjectMapper()
        assertEquals(mapper.readTree(expected.toString()), mapper.readTree(getPaymentDataRequest.toString()))
    }
}
