package co.omise.android.ui

import android.content.Context
import android.text.InputType
import android.util.AttributeSet


class CardNameEditText : OmiseEditText {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
    }

    override fun validate(): List<InvalidationType> {
        val value = text.toString().trim { it <= ' ' }
        val empty = if (value.isEmpty()) {
            InvalidationType.Empty
        } else {
            null
        }
        return listOfNotNull(empty)
    }
}
