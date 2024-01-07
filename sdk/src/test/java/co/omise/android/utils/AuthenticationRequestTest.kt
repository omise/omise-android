package co.omise.android.utils

import co.omise.android.models.Authentication
import co.omise.android.models.Serializer
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthenticationRequestTest {
    @Test
    fun buildRequest_payloadShouldMatchWithGivenParameters() {
        val request =
            Authentication.AuthenticationRequestBuilder()
                .authorizeUrl("https://www.omise.co/pay")
                .areq(
                    Authentication.AuthenticationRequestBuilder.AReq(
                        sdkAppID = "co.omise.app",
                        sdkEphemPubKey =
                            Authentication.AuthenticationRequestBuilder.SdkEphemPubKey(
                                kty = "EC",
                                x = "xxx",
                                y = "xxx",
                                crv = "P-256",
                            ),
                        sdkTransID = "7f101033-df46-4f5c-9e96-9575c924e1e7",
                        sdkMaxTimeout = 5,
                    ),
                )
                .deviceInfo(
                    Serializer().objectMapper.readValue(
                        "{}",
                        Map::class.java,
                    ) as Map<String, Any>,
                )
                .build()

        assertEquals("POST", request.method)
        assertEquals("https://www.omise.co/pay", request.url.toString())
        assertRequestBodyEquals(
            """
    {
        "areq": {
            "sdkAppID": "co.omise.app",
            "sdkEphemPubKey": {
                "kty": "EC",
                "x": "xxx",
                "y": "xxx",
                "crv": "P-256"
            },
            "sdkTransID": "7f101033-df46-4f5c-9e96-9575c924e1e7",
            "sdkMaxTimeout": 5
        },
        "device_info": {},
        "device_type": "Android"
    }
    """,
            request.payload!!,
        )
    }
}
