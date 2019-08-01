package co.omise.android.extensions

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.APIError
import co.omise.android.models.Serializer
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class APIErrorExtensionsTest {

    private val resources = ApplicationProvider.getApplicationContext<Application>().resources
    private val serializer = Serializer()
    @Test
    fun getMessageFromResources_invalidCardNumber() {
        val response = """
           {
              "object": "error",
              "location": "https://www.omise.co/api-errors#invalid-card",
              "code": "invalid_card",
              "message": "number can't be blank and brand not supported (unknown)"
            }
        """.trimIndent()
        val error = serializer.deserialize(response.byteInputStream(), APIError::class.java)

        val actualMessage = error.getMessageFromResources(resources)

        assertEquals(resources.getString(R.string.error_api_invalid_card_invalid_card_number), actualMessage)
    }

    @Test
    fun getMessageFromResources_invalidExpiryDate() {
        val response = """
           {
              "object": "error",
              "location": "https://www.omise.co/api-errors#invalid-card",
              "code": "invalid_card",
              "message": "expiration date cannot be in the past"
            }
        """.trimIndent()
        val error = serializer.deserialize(response.byteInputStream(), APIError::class.java)

        val actualMessage = error.getMessageFromResources(resources)

        assertEquals(resources.getString(R.string.error_api_invalid_card_invalid_expiry_date), actualMessage)
    }

    @Test
    fun getMessageFromResources_emptyCardHolderName() {
        val response = """
            {
              "object": "error",
              "location": "https://www.omise.co/api-errors#invalid-card",
              "code": "invalid_card",
              "message": "name can't be blank"
            }
        """.trimIndent()
        val error = serializer.deserialize(response.byteInputStream(), APIError::class.java)

        val actualMessage = error.getMessageFromResources(resources)

        assertEquals(resources.getString(R.string.error_api_invalid_card_empty_card_holder_name), actualMessage)
    }

    @Test
    fun getMessageFromResources_unsupportedBrand() {
        val response = """
            {
              "object": "error",
              "location": "https://www.omise.co/api-errors#invalid-card",
              "code": "invalid_card",
              "message": "brand not supported (unknown)"
            }
        """.trimIndent()
        val error = serializer.deserialize(response.byteInputStream(), APIError::class.java)

        val actualMessage = error.getMessageFromResources(resources)

        assertEquals(resources.getString(R.string.error_api_invalid_card_unsopported_brand), actualMessage)
    }

    @Test
    fun getMessageFromResources_otherError() {
        val response = """
            {
              "object": "error",
              "location": "https://www.omise.co/api-errors#invalid-card",
              "code": "invalid_card",
              "message": "something when wrong"
            }
        """.trimIndent()
        val error = serializer.deserialize(response.byteInputStream(), APIError::class.java)

        val actualMessage = error.getMessageFromResources(resources)

        assertEquals(resources.getString(R.string.error_required, "something when wrong"), actualMessage)
    }

    @Test
    fun getMessageFromResources_authenticationFailure() {
        val response = """
            {
              "object": "error",
              "location": "https://www.omise.co/api-errors#authentication-failure",
              "code": "authentication_failure",
              "message": "authentication failed"
            }
        """.trimIndent()
        val error = serializer.deserialize(response.byteInputStream(), APIError::class.java)

        val actualMessage = error.getMessageFromResources(resources)

        assertEquals(resources.getString(R.string.error_api_authentication_failure), actualMessage)
    }
}
