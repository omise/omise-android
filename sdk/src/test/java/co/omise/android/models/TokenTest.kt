package co.omise.android.models

import co.omise.android.utils.assertRequestBodyEquals
import org.junit.Test

class TokenTest {
    @Test
    fun createToken_shouldHaveCorrectPayload() {
        val cardParam =
            CardParam(
                number = "4242424242424242",
                name = "John Doe",
                expirationMonth = 12,
                expirationYear = 2034,
                securityCode = "123",
                country = "TH",
            )

        val request = Token.CreateTokenRequestBuilder(card = cardParam).build()

        assertRequestBodyEquals(
            """
            {
               "card":{
                  "number":"4242424242424242",
                  "name":"John Doe",
                  "expiration_month":12,
                  "expiration_year":2034,
                  "security_code":"123",
                  "country":"TH"
               }
            } 
            """.trimIndent(),
            request.payload!!,
        )
    }

    @Test
    fun createToken_shouldHaveCorrectPayloadIfBillingAddressParamsAreProvided() {
        val cardParam =
            CardParam(
                number = "4242424242424242",
                name = "John Doe",
                expirationMonth = 12,
                expirationYear = 2034,
                securityCode = "123",
                country = "US",
                state = "New York",
                city = "Strykersville",
                street1 = "311 Sanders Hill Rd",
                postalCode = "14145",
            )

        val request = Token.CreateTokenRequestBuilder(card = cardParam).build()

        assertRequestBodyEquals(
            """
            {
               "card":{
                  "number":"4242424242424242",
                  "name":"John Doe",
                  "expiration_month":12,
                  "expiration_year":2034,
                  "security_code":"123",
                  "country":"US",
                  "city":"Strykersville",
                  "state":"New York",
                  "street1":"311 Sanders Hill Rd",
                  "postal_code":"14145"
               }
            } 
            """.trimIndent(),
            request.payload!!,
        )
    }

    @Test
    fun createToken_shouldHaveCorrectPayloadIfStreet2IsProvided() {
        val cardParam =
            CardParam(
                number = "4242424242424242",
                name = "John Doe",
                expirationMonth = 12,
                expirationYear = 2034,
                securityCode = "123",
                country = "UK",
                state = "Brighton and Hove",
                city = "Brighton",
                street1 = "4/5 Pavilion Buildings",
                street2 = "Brighton",
                postalCode = "BN1 1EE",
            )

        val request = Token.CreateTokenRequestBuilder(card = cardParam).build()

        assertRequestBodyEquals(
            """
            {
               "card":{
                  "number":"4242424242424242",
                  "name":"John Doe",
                  "expiration_month":12,
                  "expiration_year":2034,
                  "security_code":"123",
                  "country":"UK",
                  "city":"Brighton",
                  "state":"Brighton and Hove",
                  "street1":"4/5 Pavilion Buildings",
                  "street2":"Brighton",
                  "postal_code":"BN1 1EE"
               }
            } 
            """.trimIndent(),
            request.payload!!,
        )
    }
}
