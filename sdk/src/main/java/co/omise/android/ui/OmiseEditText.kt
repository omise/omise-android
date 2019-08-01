package co.omise.android.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText


open class OmiseEditText : AppCompatEditText {

    val isValid: Boolean
        get() =
            try {
                validate()
                true
            } catch (e: InputValidationException) {
                false
            }

    @Throws(InputValidationException::class)
    open fun validate() {
        val value = text.toString().trim()
        if (value.isEmpty()) {
            throw InputValidationException.EmptyInputException
        }
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}

sealed class InputValidationException : Exception() {
    object EmptyInputException : InputValidationException()
    object InvalidInputException : InputValidationException()
}
