package co.omise.android.ui

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import co.omise.android.CardNumber
import co.omise.android.R


class SecurityCodeEditText : OmiseEditText {
    companion object {
        private const val CVV_LENGTH = 3
        private const val CVV_REGEX = "[0-9]{3}"
    }

    val securityCode: String
        get() = text.toString().trim()

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        filters = arrayOf(InputFilter.LengthFilter(CVV_LENGTH))
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    override fun validate() {
        super.validate()

        val value = text.toString().trim { it <= ' ' }
        if (!CVV_REGEX.toRegex().matches(value)) {
            throw InputValidationException.InvalidInputException
        }
    }
}
