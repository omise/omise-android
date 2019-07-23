package co.omise.android.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import co.omise.android.CardNumber

class CreditCardEditText : OmiseEditText {

    companion object {
        private const val CARD_NUMBER_LENGTH = 16
        private const val CARD_NUMBER_WITH_SPACE_LENGTH = 19
        private const val SEPARATOR = " "
    }

    private var cardBrandImage: Bitmap? = null
    private var cardBrandImagePaint: Paint? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        filters = arrayOf(InputFilter.LengthFilter(CARD_NUMBER_WITH_SPACE_LENGTH))
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
                    e.insert(e.length - 1, SEPARATOR)
                } else if (c == ' ') {
                    // Delete space bar
                    e.delete(e.length - 1, e.length)
                }

                updateCardBrandImage()
            }
        })

        cardBrandImagePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        cardBrandImage?.let {
            val imageLeftPosition = width.toFloat() - it.width
            val imageTopPosition = (height - it.height) / 2f
            canvas?.drawBitmap(it, imageLeftPosition, imageTopPosition, cardBrandImagePaint)
        }
    }

    override fun validate() {
        super.validate()

        val value = text.toString().trim { it <= ' ' }.replace(SEPARATOR, "")
        if (value.length < CARD_NUMBER_LENGTH || !CardNumber.luhn(value)) {
            throw InputValidationException.InvalidInputException
        }
    }

    private fun updateCardBrandImage() {
        val number = text.toString()
        if (number.length > 6) {
            val brand = CardNumber.brand(number)
            if (brand != null && brand.logoResourceId > -1) {
                cardBrandImage = BitmapFactory.decodeResource(resources, brand.logoResourceId)
                return
            }
        }
        cardBrandImage = null
    }
}
