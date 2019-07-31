package co.omise.android.extensions

import android.content.res.Resources
import co.omise.android.R
import co.omise.android.models.APIError


fun APIError.getErrorMessage(res: Resources): String = when (code) {
    "invalid_card" -> {
        when {
            message.contains("number") -> res.getString(R.string.error_api_invalid_card_invalid_card_number)
            message.contains("expiration") -> res.getString(R.string.error_api_invalid_card_invalid_expiry_date)
            message.contains("name") -> res.getString(R.string.error_api_invalid_card_empty_card_holder_name)
            message.contains("brand") -> res.getString(R.string.error_api_invalid_card_unsopported_brand)
            else -> res.getString(R.string.error_required, message)
        }
    }
    "authentication_failure" -> res.getString(R.string.error_api_authentication_failure)
    else -> res.getString(R.string.error_required, message)
}
