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

        assertEquals(resources.getString(R.string.error_api_invalid_card_invalid_expiration_date), actualMessage)
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

        assertEquals(resources.getString(R.string.error_api_invalid_card_unsupported_brand), actualMessage)
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

    @Test
    fun createInvalidCardReasons_createFromErrorMessages() {
        val errorMessagesWithErrorReasons = listOf(
                Pair("number can't be blank and brand not supported (unknown)", InvalidCardReason.InvalidCardNumber),
                Pair("expiration date cannot be in the past", InvalidCardReason.InvalidExpirationDate),
                Pair("name can't be blank", InvalidCardReason.EmptyCardHolderName),
                Pair("brand not supported (unknown)", InvalidCardReason.UnsupportedBrand),
                Pair("something when wrong", InvalidCardReason.Unknown("something when wrong"))
        )

        errorMessagesWithErrorReasons.forEach {
            assertEquals(it.second, InvalidCardReason.creator(it.first))
        }
    }

    @Test
    fun createBadRequestReasons_createFromErrorMessages() {
        val errorMessagesWithErrorReasons = listOf(
                Pair("amount must be at least 150", BadRequestReason.AmountIsLessThanValidAmount(150)),
                Pair("amount must be greater than 150", BadRequestReason.AmountIsLessThanValidAmount(150)),
                Pair("amount must be less than 50000", BadRequestReason.AmountIsGreaterThanValidAmount(50000)),
                Pair("currency must be JPY", BadRequestReason.InvalidCurrency),
                Pair("name cannot be blank", BadRequestReason.EmptyName),
                Pair("name is too long (maximum is 10 characters)", BadRequestReason.NameIsTooLong(10)),
                Pair("email is in invalid format", BadRequestReason.InvalidEmail),
                Pair("and phone_number must contain 10-11 digit characters", BadRequestReason.InvalidPhoneNumber)
        )

        errorMessagesWithErrorReasons.forEach {
            assertEquals(it.second, BadRequestReason.creator(it.first))
        }
    }
}
