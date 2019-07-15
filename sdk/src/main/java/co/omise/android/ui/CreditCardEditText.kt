package co.omise.android.ui

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet

class CreditCardEditText : OmiseEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(19))
        inputType = InputType.TYPE_CLASS_PHONE

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(e: Editable) {
                if (e.isEmpty() || e.length % 5 != 0) {
                    return
                }

                val c = e[e.length - 1]
                if (Character.isDigit(c)) {
                    // Insert space bar
                    e.insert(e.length - 1, " ")
                } else if (c == ' ') {
                    // Delete space bar
                    e.delete(e.length - 1, e.length)
                }
            }
        })
    }

}
