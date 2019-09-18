package co.omise.android.extensions

import android.content.res.Resources
import co.omise.android.R
import co.omise.android.models.APIError


fun APIError.getMessageFromResources(res: Resources): String = result@ when (errorCode) {
    is APIErrorCode.InvalidCard -> {
       return@result (errorCode as APIErrorCode.InvalidCard).reasons.forEach {
            return when (it) {
                InvalidCardReason.InvalidCardNumber -> res.getString(R.string.error_api_invalid_card_invalid_card_number)
                InvalidCardReason.InvalidExpirationDate -> res.getString(R.string.error_api_invalid_card_invalid_expiry_date)
                InvalidCardReason.EmptyCardHolderName -> res.getString(R.string.error_api_invalid_card_empty_card_holder_name)
                InvalidCardReason.UnsupportedBrand -> res.getString(R.string.error_api_invalid_card_unsupported_brand)
                is InvalidCardReason.Unknown -> res.getString(R.string.error_required, it.message)
            }
        }
    }
    is APIErrorCode.BadRequest -> {
        return@result (errorCode as APIErrorCode.BadRequest).reasons.forEach {
            return when (it) {
                is BadRequestReason.AmountIsGreaterThanValidAmount -> res.getString(R.string.error_api_bad_request_amount_is_greater_than_valid_amount_with_valid_amount, "10000 thb")
                is BadRequestReason.AmountIsLessThanValidAmount -> res.getString(R.string.error_api_bad_request_amount_is_less_than_valid_amount_with_valid_amount, "10000 thb")
                BadRequestReason.InvalidCurrency -> res.getString(R.string.error_api_bad_request_invalid_currency)
                BadRequestReason.EmptyName -> res.getString(R.string.error_api_bad_request_empty_name)
                is BadRequestReason.NameIsTooLong -> res.getString(R.string.error_api_bad_request_name_is_too_long_with_valid_length, 1)
                BadRequestReason.InvalidName -> res.getString(R.string.error_api_bad_request_invalid_name)
                BadRequestReason.InvalidEmail -> res.getString(R.string.error_api_bad_request_invalid_email)
                BadRequestReason.InvalidPhoneNumber -> res.getString(R.string.error_api_bad_request_invalid_phone_number)
                BadRequestReason.TypeNotSupported -> res.getString(R.string.error_api_bad_request_type_not_supported)
                BadRequestReason.CurrencyNotSupported -> res.getString(R.string.error_api_bad_request_currency_not_supported)
                is BadRequestReason.Unknown -> res.getString(R.string.error_api_bad_request_other, it.message)
            }
        }
    }
    APIErrorCode.AuthenticationFailure -> res.getString(R.string.error_api_authentication_failure)
    else -> res.getString(R.string.error_required, message)
}

val APIError.errorCode: APIErrorCode
    get() = APIErrorCode.creator(code.orEmpty(), message.orEmpty())

sealed class APIErrorCode {
    object AuthenticationFailure : APIErrorCode()
    data class InvalidCard(val reasons: List<InvalidCardReason>) : APIErrorCode()
    data class BadRequest(val reasons: List<BadRequestReason>) : APIErrorCode()
    data class Unknown(val code: String?) : APIErrorCode()

    companion object {
        fun creator(code: String, message: String): APIErrorCode {
            val messages = message.split(",")
            return when (code) {
                "authentication_failure" -> AuthenticationFailure
                "invalid_card" -> InvalidCard(messages.map { InvalidCardReason.creator(it) })
                "bad_request" -> BadRequest(messages.map { BadRequestReason.creator(it) })
                else -> Unknown(code)
            }
        }
    }
}

sealed class InvalidCardReason {
    object InvalidCardNumber : InvalidCardReason()
    object InvalidExpirationDate : InvalidCardReason()
    object EmptyCardHolderName : InvalidCardReason()
    object UnsupportedBrand : InvalidCardReason()
    data class Unknown(val message: String?) : InvalidCardReason()

    companion object {
        fun creator(message: String): InvalidCardReason = when {
            message.isContains("number") -> InvalidCardNumber
            message.isContains("expiration") -> InvalidExpirationDate
            message.isContains("name") -> EmptyCardHolderName
            message.isContains("brand") -> UnsupportedBrand
            else -> Unknown(message)
        }
    }
}

sealed class BadRequestReason {
    data class AmountIsGreaterThanValidAmount(val validAmount: Long, val currency: String) : BadRequestReason()
    data class AmountIsLessThanValidAmount(val validAmount: Long, val currency: String) : BadRequestReason()
    object InvalidCurrency : BadRequestReason()
    object EmptyName : BadRequestReason()
    data class NameIsTooLong(val maximum: Int) : BadRequestReason()
    object InvalidName : BadRequestReason()
    object InvalidEmail : BadRequestReason()
    object InvalidPhoneNumber : BadRequestReason()
    object TypeNotSupported : BadRequestReason()
    object CurrencyNotSupported : BadRequestReason()
    data class Unknown(val message: String?) : BadRequestReason()

    companion object {
        fun creator(message: String): BadRequestReason = when {
            message.isContains("currency must be") -> InvalidCurrency
            // when {
            // AmountIsGreaterThanValidAmount
            // AmountIsLessThanValidAmount
            // }
            message.isContains("type") -> TypeNotSupported
            message.isContains("currency") -> CurrencyNotSupported
            message.isContains("name") && message.isContains("blank") -> EmptyName
            message.startsWith("name is too long") -> NameIsTooLong(0)
            message.isContains("name") -> InvalidName
            message.isContains("email") -> InvalidEmail
            message.isContains("phone") -> InvalidPhoneNumber
            else -> Unknown(message)
        }
    }
}
