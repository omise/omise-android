package co.omise.android.ui

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet

/**
 * SecurityCodeEditText is a custom EditText for credit card security code field. This
 * EditText applies the security code range limitation and InputType.
 */
class SecurityCodeEditText : OmiseEditText {
    companion object {
        private const val CVV_LENGTH = 4
        private const val CVV_REGEX = "[0-9]{3,4}"
    }

    val securityCode: String
        get() = text.toString().trim()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        filters = arrayOf(InputFilter.LengthFilter(CVV_LENGTH))
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    override fun validate() {
        super.validate()

        val value = text.toString().trim()
        if (!CVV_REGEX.toRegex().matches(value)) {
            throw InputValidationException.InvalidInputException
        }
    }
}
