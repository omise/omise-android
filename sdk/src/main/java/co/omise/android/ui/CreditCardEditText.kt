package co.omise.android.ui

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet

class CreditCardEditText : OmiseEditText {

    companion object {
        private const val CARD_NUMBER_WITH_GUTTER_CHARS_LENGTH = 19
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        filters = arrayOf(InputFilter.LengthFilter(CARD_NUMBER_WITH_GUTTER_CHARS_LENGTH))
//        inputType = InputType.TYPE_CLASS_NUMBER

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
