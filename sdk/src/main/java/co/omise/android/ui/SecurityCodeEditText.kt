package co.omise.android.ui

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet


class SecurityCodeEditText : OmiseEditText {
    companion object {
        private const val CVV_LENGTH = 3
    }

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
}
