package co.omise.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import co.omise.android.CardNumber
import co.omise.android.R
import co.omise.android.models.CardBrand

/**
 * CreditCardEditText is a custom EditText for the credit card number field. This EditText
 * both formats the card number and also draws the card brand logo for the given
 * card number.
 */
class CreditCardEditText : OmiseEditText {
    companion object {
        private const val CARD_NUMBER_WITH_SPACE_LENGTH = 23
        private const val SEPARATOR = " "
    }

    private var cardBrandImage: Drawable? = null
    private var cardBrandIconSize: Int = 0

    val cardNumber: String
        get() = text.toString().trim().replace(SEPARATOR, "")

    val cardBrand: CardBrand?
        get() = CardNumber.brand(cardNumber)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        filters = arrayOf(InputFilter.LengthFilter(CARD_NUMBER_WITH_SPACE_LENGTH))
        inputType = InputType.TYPE_CLASS_PHONE

        addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int,
                ) {
                    // Do nothing
                }

                override fun onTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int,
                ) {
                    // Do nothing
                }

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
            },
        )

        cardBrandIconSize = resources.getDimensionPixelSize(R.dimen.card_brand_icon_size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        cardBrandImage?.let {
            val targetHeight = cardBrandIconSize
            val ratio = it.intrinsicWidth.toFloat() / it.intrinsicHeight.toFloat()
            val targetWidth = (targetHeight * ratio).toInt()

            val imageLeftPosition = width - targetWidth - paddingRight
            val imageTopPosition = (height - targetHeight) / 2
            it.setBounds(
                imageLeftPosition,
                imageTopPosition,
                imageLeftPosition + targetWidth,
                imageTopPosition + targetHeight,
            )
            it.draw(canvas)
        }
    }

    override fun validate() {
        super.validate()

        val brand = cardBrand ?: throw InputValidationException.InvalidInputException

        if (!CardNumber.luhn(cardNumber) || !brand.valid(cardNumber)) {
            throw InputValidationException.InvalidInputException
        }
    }

    private fun updateCardBrandImage() {
        val number = text.toString()
        if (number.length > 6) {
            val brand = CardNumber.brand(number)
            if (brand != null && brand.logoResourceId > -1) {
                cardBrandImage = ContextCompat.getDrawable(context, brand.logoResourceId)
                return
            }
        }
        cardBrandImage = null
    }
}
