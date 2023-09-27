package co.omise.android.utils

import co.omise.android.models.Authentication
import org.junit.Test


class AuthenticationRequestTest {
    @Test
    fun buildRequest_payloadShouldMatchWithGivenParameters() {
        val request = Authentication.AuthenticationRequestBuilder(
            authorizeUrl = "https://www.omise.co/pay",
            areq = Authentication.AuthenticationRequestBuilder.AReq(
                sdkAppID = "co.omise.app",
                sdkEphemPubKey = "{\"kty\":\"EC\",\"x\":\"xxx\",\"y\":\"xxx\",\"crv\":\"P-256\"}",
                sdkTransID = "7f101033-df46-4f5c-9e96-9575c924e1e7",
                sdkMaxTimeout = 5
            ),
            deviceInfo = "eyJhbGciOiJSU0EtT0FFUC0yNTYiLCJlbmMi",
        ).build()

        assertRequestBodyEquals(
            "{\"authorizeUrl\":\"https://www.omise.co/pay\",\"areq\":{\"sdkAppID\":\"co.omise.app\",\"sdkEphemPubKey\":\"{\\\"kty\\\":\\\"EC\\\",\\\"x\\\":\\\"xxx\\\",\\\"y\\\":\\\"xxx\\\",\\\"crv\\\":\\\"P-256\\\"}\",\"sdkTransID\":\"7f101033-df46-4f5c-9e96-9575c924e1e7\",\"sdkMaxTimeout\":5},\"device_info\":\"eyJhbGciOiJSU0EtT0FFUC0yNTYiLCJlbmMi\",\"device_type\":\"Android\"}",
            request.payload!!
        )
    }
}
