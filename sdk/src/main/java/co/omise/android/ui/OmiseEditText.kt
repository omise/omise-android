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

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    var errorMessage: String? = null
        set(value) {
            field = value
            invalidate()
        }

    private fun init() {

        errorText = TextPaint().apply {
            color = Color.RED
            textSize = 14f
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

            canvas?.drawText(
                    errorMessage ?: "",
                    0f,
                    y + height,
                    errorText
            )
    }

}
