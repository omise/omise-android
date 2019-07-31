package co.omise.android.extensions

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.APIError
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class APIErrorExtensionsTest {

    private val resources = ApplicationProvider.getApplicationContext<Application>().resources

    @Test
    fun getMessageFromResources_invalidCardNumber() {
        val error = APIError("""
           {
              "object": "error",
              "location": "https://www.omise.co/api-errors#invalid-card",
              "code": "invalid_card",
              "message": "number can't be blank and brand not supported (unknown)"
            }
        """.trimIndent())

        val actualError = error.getMessageFromResources(resources)

        assertEquals(resources.getString(R.string.error_api_invalid_card_invalid_card_number), actualError)
    }

    @Test
    fun getMessageFromResources_invalidExpiryDate() {
        val error = APIError("""
           {
              "object": "error",
              "location": "https://www.omise.co/api-errors#invalid-card",
              "code": "invalid_card",
              "message": "expiration date cannot be in the past"
            }
        """.trimIndent())

        val actualError = error.getMessageFromResources(resources)

        assertEquals(resources.getString(R.string.error_api_invalid_card_invalid_expiry_date), actualError)
    }

    @Test
    fun getMessageFromResources_emptyCardHolderName() {
        val error = APIError("""
            {
              "object": "error",
              "location": "https://www.omise.co/api-errors#invalid-card",
              "code": "invalid_card",
              "message": "name can't be blank"
            }
        """.trimIndent())

        val actualError = error.getMessageFromResources(resources)

        assertEquals(resources.getString(R.string.error_api_invalid_card_empty_card_holder_name), actualError)
    }

    @Test
    fun getMessageFromResources_unsupportedBrand() {
        val error = APIError("""
            {
              "object": "error",
              "location": "https://www.omise.co/api-errors#invalid-card",
              "code": "invalid_card",
              "message": "brand not supported (unknown)"
            }
        """.trimIndent())

        val actualError = error.getMessageFromResources(resources)

        assertEquals(resources.getString(R.string.error_api_invalid_card_unsopported_brand), actualError)
    }

    @Test
    fun getMessageFromResources_otherError() {
        val error = APIError("""
            {
              "object": "error",
              "location": "https://www.omise.co/api-errors#invalid-card",
              "code": "invalid_card",
              "message": "something when wrong"
            }
        """.trimIndent())

        val actualError = error.getMessageFromResources(resources)

        assertEquals(resources.getString(R.string.error_required, "something when wrong"), actualError)
    }

    @Test
    fun getMessageFromResources_authenticationFailure() {
        val error = APIError("""
            {
              "object": "error",
              "location": "https://www.omise.co/api-errors#authentication-failure",
              "code": "authentication_failure",
              "message": "authentication failed"
            }
        """.trimIndent())

        val actualError = error.getMessageFromResources(resources)

        assertEquals(resources.getString(R.string.error_api_authentication_failure), actualError)
    }
}
