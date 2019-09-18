package co.omise.android.extensions

import android.content.res.Resources
import co.omise.android.R
import co.omise.android.models.APIError


fun APIError.getMessageFromResources(res: Resources): String = result@ when (errorCode) {
    is APIErrorCode.InvalidCard -> {
        return@result message?.split(",")?.forEach {
            return when (val errorReason = InvalidCardReason.creator(it)) {
                InvalidCardReason.InvalidCardNumber -> res.getString(R.string.error_api_invalid_card_invalid_card_number)
                InvalidCardReason.InvalidExpirationDate -> res.getString(R.string.error_api_invalid_card_invalid_expiry_date)
                InvalidCardReason.EmptyCardHolderName -> res.getString(R.string.error_api_invalid_card_empty_card_holder_name)
                InvalidCardReason.UnsupportedBrand -> res.getString(R.string.error_api_invalid_card_unsopported_brand)
                is InvalidCardReason.Unknown -> res.getString(R.string.error_required, errorReason.message)
            }
        } ?: return@result res.getString(R.string.error_required, message)
    }
    APIErrorCode.AuthenticationFailure -> res.getString(R.string.error_api_authentication_failure)
    else -> res.getString(R.string.error_required, message)
}

val APIError.errorCode: APIErrorCode
    get() = when (code) {
        "authentication_failure" -> APIErrorCode.AuthenticationFailure
        "invalid_card" -> APIErrorCode.InvalidCard(emptyList())
        "bad_request" -> APIErrorCode.BadRequest(emptyList())
        else -> APIErrorCode.Unknown(code)
    }

sealed class APIErrorCode {
    object AuthenticationFailure : APIErrorCode()
    data class InvalidCard(val reasons: List<BadRequestReason>) : APIErrorCode()
    data class BadRequest(val reasons: List<BadRequestReason>) : APIErrorCode()
    data class Unknown(val errorCode: String?) : APIErrorCode()
}

sealed class InvalidCardReason {
    object InvalidCardNumber : InvalidCardReason()
    object InvalidExpirationDate : InvalidCardReason()
    object EmptyCardHolderName : InvalidCardReason()
    object UnsupportedBrand : InvalidCardReason()
    data class Unknown(val message: String?) : InvalidCardReason()

    companion object {
        fun creator(errorMessage: String): InvalidCardReason = when {
            errorMessage.isContains("number") -> InvalidCardNumber
            errorMessage.isContains("expiration") -> InvalidExpirationDate
            errorMessage.isContains("name") -> EmptyCardHolderName
            errorMessage.isContains("brand") -> UnsupportedBrand
            else -> Unknown(errorMessage)
        }
    }
}

sealed class BadRequestReason {

}
