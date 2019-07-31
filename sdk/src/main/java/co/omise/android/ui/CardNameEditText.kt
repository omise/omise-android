package co.omise.android.ui

import android.content.Context
import android.text.InputType
import android.util.AttributeSet


class CardNameEditText : OmiseEditText {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val cardName: String
        get() = text.toString().trim()

    init {
        inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
    }
}
