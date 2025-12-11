package co.omise.android.extensions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class APIErrorCodeTest {
    
    @Test
    fun apiErrorCode_authenticationFailure_shouldReturnCorrectType() {
        val errorCode = APIErrorCode.creator("authentication_failure", "")
        
        assertTrue(errorCode is APIErrorCode.AuthenticationFailure)
    }
    
    @Test
    fun apiErrorCode_invalidCard_shouldReturnInvalidCardWithReasons() {
        val errorCode = APIErrorCode.creator("invalid_card", "number is invalid")
        
        assertTrue(errorCode is APIErrorCode.InvalidCard)
        val invalidCard = errorCode as APIErrorCode.InvalidCard
        assertEquals(1, invalidCard.reasons.size)
        assertTrue(invalidCard.reasons[0] is InvalidCardReason.InvalidCardNumber)
    }
    
    @Test
    fun apiErrorCode_invalidCard_multipleReasons_shouldParseAll() {
        val errorCode = APIErrorCode.creator(
            "invalid_card",
            "number is invalid, and expiration date is invalid"
        )
        
        assertTrue(errorCode is APIErrorCode.InvalidCard)
        val invalidCard = errorCode as APIErrorCode.InvalidCard
        assertEquals(2, invalidCard.reasons.size)
        assertTrue(invalidCard.reasons[0] is InvalidCardReason.InvalidCardNumber)
        assertTrue(invalidCard.reasons[1] is InvalidCardReason.InvalidExpirationDate)
    }
    
    @Test
    fun apiErrorCode_badRequest_shouldReturnBadRequestWithReasons() {
        val errorCode = APIErrorCode.creator("bad_request", "currency must be thb")
        
        assertTrue(errorCode is APIErrorCode.BadRequest)
        val badRequest = errorCode as APIErrorCode.BadRequest
        assertEquals(1, badRequest.reasons.size)
        assertTrue(badRequest.reasons[0] is BadRequestReason.InvalidCurrency)
    }
    
    @Test
    fun apiErrorCode_serviceNotFound_shouldReturnCorrectType() {
        val errorCode = APIErrorCode.creator("service_not_found", "")
        
        assertTrue(errorCode is APIErrorCode.ServiceNotFound)
    }
    
    @Test
    fun apiErrorCode_unknown_shouldReturnUnknownWithCode() {
        val errorCode = APIErrorCode.creator("some_unknown_error", "message")
        
        assertTrue(errorCode is APIErrorCode.Unknown)
        val unknown = errorCode as APIErrorCode.Unknown
        assertEquals("some_unknown_error", unknown.code)
    }
    
    @Test
    fun invalidCardReason_invalidCardNumber_shouldCreateCorrectType() {
        val reason = InvalidCardReason.creator("card number is invalid")
        
        assertTrue(reason is InvalidCardReason.InvalidCardNumber)
    }
    
    @Test
    fun invalidCardReason_invalidExpirationDate_shouldCreateCorrectType() {
        val reason = InvalidCardReason.creator("expiration date is invalid")
        
        assertTrue(reason is InvalidCardReason.InvalidExpirationDate)
    }
    
    @Test
    fun invalidCardReason_emptyCardHolderName_shouldCreateCorrectType() {
        val reason = InvalidCardReason.creator("name is required")
        
        assertTrue(reason is InvalidCardReason.EmptyCardHolderName)
    }
    
    @Test
    fun invalidCardReason_unsupportedBrand_shouldCreateCorrectType() {
        val reason = InvalidCardReason.creator("brand is not supported")
        
        assertTrue(reason is InvalidCardReason.UnsupportedBrand)
    }
    
    @Test
    fun invalidCardReason_unknown_shouldCreateUnknownWithCapitalizedMessage() {
        val reason = InvalidCardReason.creator("some unknown reason")
        
        assertTrue(reason is InvalidCardReason.Unknown)
        val unknown = reason as InvalidCardReason.Unknown
        assertEquals("Some unknown reason", unknown.message)
    }
    
    @Test
    fun badRequestReason_amountIsGreaterThanValid_shouldParse() {
        val reason = BadRequestReason.creator("amount must be less than 100000 thb")
        
        assertTrue(reason is BadRequestReason.AmountIsGreaterThanValidAmount)
        val amountReason = reason as BadRequestReason.AmountIsGreaterThanValidAmount
        assertEquals(100000L, amountReason.validAmount)
        assertEquals("thb", amountReason.currency)
    }
    
    @Test
    fun badRequestReason_amountIsLessThanValid_shouldParse() {
        val reason = BadRequestReason.creator("amount must be at least 1000 thb")
        
        assertTrue(reason is BadRequestReason.AmountIsLessThanValidAmount)
        val amountReason = reason as BadRequestReason.AmountIsLessThanValidAmount
        assertEquals(1000L, amountReason.validAmount)
        assertEquals("thb", amountReason.currency)
    }
    
    @Test
    fun badRequestReason_invalidCurrency_shouldCreateCorrectType() {
        val reason = BadRequestReason.creator("currency must be thb")
        
        assertTrue(reason is BadRequestReason.InvalidCurrency)
    }
    
    @Test
    fun badRequestReason_emptyName_shouldCreateCorrectType() {
        val reason = BadRequestReason.creator("name can't be blank")
        
        assertTrue(reason is BadRequestReason.EmptyName)
    }
    
    @Test
    fun badRequestReason_nameIsTooLong_shouldParseMaximum() {
        val reason = BadRequestReason.creator("name is too long (maximum is 255 characters)")
        
        assertTrue(reason is BadRequestReason.NameIsTooLong)
        val nameTooLong = reason as BadRequestReason.NameIsTooLong
        assertEquals(255, nameTooLong.maximum)
    }
    
    @Test
    fun badRequestReason_invalidName_shouldCreateCorrectType() {
        val reason = BadRequestReason.creator("name is invalid")
        
        assertTrue(reason is BadRequestReason.InvalidName)
    }
    
    @Test
    fun badRequestReason_invalidEmail_shouldCreateCorrectType() {
        val reason = BadRequestReason.creator("email is invalid")
        
        assertTrue(reason is BadRequestReason.InvalidEmail)
    }
    
    @Test
    fun badRequestReason_invalidPhoneNumber_shouldCreateCorrectType() {
        val reason = BadRequestReason.creator("phone number is invalid")
        
        assertTrue(reason is BadRequestReason.InvalidPhoneNumber)
    }
    
    @Test
    fun badRequestReason_sourceTypeNotSupported_shouldCreateCorrectType() {
        val reason = BadRequestReason.creator("source type is not supported")
        
        assertTrue(reason is BadRequestReason.SourceTypeNotSupported)
    }
    
    @Test
    fun badRequestReason_currencyNotSupported_shouldCreateCorrectType() {
        val reason = BadRequestReason.creator("currency is not supported")
        
        assertTrue(reason is BadRequestReason.CurrencyNotSupported)
    }
    
    @Test
    fun badRequestReason_unknown_shouldCreateUnknownWithCapitalizedMessage() {
        val reason = BadRequestReason.creator("some unknown error message")
        
        assertTrue(reason is BadRequestReason.Unknown)
        val unknown = reason as BadRequestReason.Unknown
        assertEquals("Some unknown error message", unknown.message)
    }
}

