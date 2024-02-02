package co.omise.android.extensions

import android.content.res.Resources
import co.omise.android.R
import co.omise.android.models.APIError
import co.omise.android.models.Amount

fun APIError.getMessageFromResources(res: Resources): String =
    when (errorCode) {
        is APIErrorCode.InvalidCard -> getMessageWhenInvalidCard(res)
        is APIErrorCode.BadRequest -> getMessageWhenBadRequest(res)
        APIErrorCode.AuthenticationFailure -> res.getString(R.string.error_api_authentication_failure)
        APIErrorCode.ServiceNotFound -> res.getString(R.string.error_api_service_not_found)
        else -> res.getString(R.string.error_required,
            message?.capitalizeFirstChar())
    }

fun APIError.getMessageWhenInvalidCard(res: Resources): String {
    return (errorCode as APIErrorCode.InvalidCard).reasons.firstOrNull().run {
        when (this) {
            InvalidCardReason.InvalidCardNumber -> res.getString(R.string.error_api_invalid_card_invalid_card_number)
            InvalidCardReason.InvalidExpirationDate -> res.getString(R.string.error_api_invalid_card_invalid_expiration_date)
            InvalidCardReason.EmptyCardHolderName -> res.getString(R.string.error_api_invalid_card_empty_card_holder_name)
            InvalidCardReason.UnsupportedBrand -> res.getString(R.string.error_api_invalid_card_unsupported_brand)
            is InvalidCardReason.Unknown -> res.getString(R.string.error_required, message)
            else ->
                res.getString(
                    R.string.error_required,
                    message?.capitalizeFirstChar() ,
                )
        }
    }
}

fun APIError.getMessageWhenBadRequest(res: Resources): String {
    return (errorCode as APIErrorCode.BadRequest).reasons.firstOrNull().run {
        when (this) {
            is BadRequestReason.AmountIsGreaterThanValidAmount ->
                if (validAmount != null && !currency.isNullOrEmpty()) {
                    val amount = Amount(validAmount, currency)
                    res.getString(
                        R.string.error_api_bad_request_amount_is_greater_than_valid_amount_with_valid_amount,
                        amount.toAmountString(),
                    )
                } else {
                    res.getString(R.string.error_api_bad_request_amount_is_greater_than_valid_amount_without_valid_amount)
                }
            is BadRequestReason.AmountIsLessThanValidAmount ->
                if (validAmount != null && !currency.isNullOrEmpty()) {
                    val amount = Amount(validAmount, currency)
                    res.getString(
                        R.string.error_api_bad_request_amount_is_less_than_valid_amount_with_valid_amount,
                        amount.toAmountString(),
                    )
                } else {
                    res.getString(R.string.error_api_bad_request_amount_is_less_than_valid_amount_without_valid_amount)
                }
            BadRequestReason.InvalidCurrency -> res.getString(R.string.error_api_bad_request_invalid_currency)
            BadRequestReason.EmptyName -> res.getString(R.string.error_api_bad_request_empty_name)
            is BadRequestReason.NameIsTooLong ->
                res.getString(
                    R.string.error_api_bad_request_name_is_too_long_with_valid_length,
                    maximum,
                )
            BadRequestReason.InvalidName -> res.getString(R.string.error_api_bad_request_invalid_name)
            BadRequestReason.InvalidEmail -> res.getString(R.string.error_api_bad_request_invalid_email)
            BadRequestReason.InvalidPhoneNumber -> res.getString(R.string.error_api_bad_request_invalid_phone_number)
            BadRequestReason.TypeNotSupported -> res.getString(R.string.error_api_bad_request_type_not_supported)
            BadRequestReason.CurrencyNotSupported -> res.getString(R.string.error_api_bad_request_currency_not_supported)
            else -> message ?: res.getString(R.string.error_unknown_without_reason)
        }
    }
}

val APIError.errorCode: APIErrorCode
    get() = APIErrorCode.creator(code.orEmpty(), message.orEmpty())

sealed class APIErrorCode {
    object AuthenticationFailure : APIErrorCode()

    data class InvalidCard(val reasons: List<InvalidCardReason>) : APIErrorCode()

    data class BadRequest(val reasons: List<BadRequestReason>) : APIErrorCode()

    object ServiceNotFound : APIErrorCode()

    data class Unknown(val code: String?) : APIErrorCode()

    companion object {
        fun creator(
            code: String,
            message: String,
        ): APIErrorCode {
            val messages =
                message.split(",")
                    .map { if (it.startsWith("and")) it.replaceFirst("and", "", false) else it }
                    .map(String::trim)
            return when (code) {
                "authentication_failure" -> AuthenticationFailure
                "invalid_card" -> InvalidCard(messages.map { InvalidCardReason.creator(it) })
                "bad_request" -> BadRequest(messages.map { BadRequestReason.creator(it) })
                "service_not_found" -> ServiceNotFound
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
        fun creator(message: String): InvalidCardReason =
            when {
                message.isContains("number") -> InvalidCardNumber
                message.isContains("expiration") -> InvalidExpirationDate
                message.isContains("name") -> EmptyCardHolderName
                message.isContains("brand") -> UnsupportedBrand
                else -> Unknown(message.capitalizeFirstChar())
            }
    }
}

sealed class BadRequestReason {
    data class AmountIsGreaterThanValidAmount(
        val validAmount: Long? = null,
        val currency: String? = null,
    ) : BadRequestReason()

    data class AmountIsLessThanValidAmount(
        val validAmount: Long? = null,
        val currency: String? = null,
    ) : BadRequestReason()

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
        private val amountAtLeastValidAmountErrorMessageRegex =
            """amount must be at least (\d+)(\s)?(([a-zA-Z]{3})?)""".toRegex()
        private val amountLessThanValidAmountErrorMessageRegex =
            """amount must be greater than (\d+)(\s)?(([a-zA-Z]{3})?)""".toRegex()
        private val amountGreaterThanValidAmountErrorMessageRegex =
            """amount must be less than (\d+)(\s)?(([a-zA-Z]{3})?)""".toRegex()
        private val nameIsTooLongErrorMessageRegex =
            """name is too long \(maximum is (\d+) characters\)""".toRegex()

        fun creator(message: String): BadRequestReason =
            when {
                message.isContains("currency must be") -> InvalidCurrency
                message.isContains("amount must be") ->
                    when {
                        message.matches(amountAtLeastValidAmountErrorMessageRegex) -> {
                            val matchedResult =
                                amountAtLeastValidAmountErrorMessageRegex.findAll(message)
                                    .toList()[0].groupValues
                            val validAmount = matchedResult.getOrNull(1)?.toLong()
                            val currency = matchedResult[3].ifEmpty { null }
                            AmountIsLessThanValidAmount(validAmount, currency)
                        }
                        message.matches(amountLessThanValidAmountErrorMessageRegex) -> {
                            val matchedResult =
                                amountLessThanValidAmountErrorMessageRegex.findAll(message)
                                    .toList()[0].groupValues
                            val validAmount = matchedResult.getOrNull(1)?.toLong()
                            val currency = matchedResult[3].ifEmpty { null }
                            AmountIsLessThanValidAmount(validAmount, currency)
                        }
                        message.matches(amountGreaterThanValidAmountErrorMessageRegex) -> {
                            val matchedResult =
                                amountGreaterThanValidAmountErrorMessageRegex.findAll(message)
                                    .toList()[0].groupValues
                            val validAmount = matchedResult.getOrNull(1)?.toLong()
                            val currency = matchedResult[3].ifEmpty { null }
                            AmountIsGreaterThanValidAmount(validAmount, currency)
                        }
                        else -> Unknown(message.capitalizeFirstChar())
                    }
                message.isContains("type") -> TypeNotSupported
                message.isContains("currency") -> CurrencyNotSupported
                message.isContains("name") && message.isContains("blank") -> EmptyName
                message.startsWith("name is too long") -> {
                    val matchedResult =
                        nameIsTooLongErrorMessageRegex.findAll(message).toList()[0].groupValues
                    NameIsTooLong(matchedResult[1].toInt())
                }
                message.isContains("name") -> InvalidName
                message.isContains("email") -> InvalidEmail
                message.isContains("phone") -> InvalidPhoneNumber
                else -> Unknown(message.capitalizeFirstChar())
            }
    }
}
