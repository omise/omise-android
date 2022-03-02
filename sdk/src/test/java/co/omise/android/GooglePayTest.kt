package co.omise.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.request.GooglePay
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GooglePayTest {
    @Test
    fun googlepay_isReadyToPayRequest_returnsCorrectValues() {
        val googlePay = GooglePay("pkey_123", arrayListOf("Visa"), 3000, "thb", "merchantId")
        val isReadyToPay = googlePay.isReadyToPayRequest()


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
        val googlePay = GooglePay("pkey_123", arrayListOf(), 3000, "thb", "merchantId")
        val isReadyToPay = googlePay.isReadyToPayRequest()


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
        val googlePay = GooglePay("pkey_123", arrayListOf("American Express", "MasterCard", "JCB"), 3000, "sgd", "merchantId")
        val getPaymentDataRequest = googlePay.getPaymentDataRequest()


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
