package co.omise.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText


open class OmiseEditText : AppCompatEditText {

    private var errorText: TextPaint? = null

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


    var errorMessage: String? = null
        set(value) {
            field = value
            invalidate()
        }

    init {
        errorText = TextPaint().apply {
            color = Color.RED
            textSize = 14f
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val xPos = paddingLeft.toFloat()
        val yPos = height - (errorText?.fontMetrics?.bottom ?: 0f)
        canvas?.drawText(
                errorMessage ?: "",
                xPos,
                yPos,
                errorText as Paint
        )
    }
}

sealed class InputValidationException : Exception() {
    object EmptyInputException : InputValidationException()
    object InvalidInputException : InputValidationException()
}
