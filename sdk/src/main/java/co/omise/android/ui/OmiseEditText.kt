package co.omise.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText


abstract class OmiseEditText : AppCompatEditText {

    private var errorText: TextPaint? = null

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

        val canvas = canvas?: return

        canvas.clipBounds
        canvas.drawText(
                errorMessage ?: "",
                0f,
                10f,
                errorText
        )
    }

}

fun AppCompatEditText.disableOptions() {
    this.customSelectionActionModeCallback = object : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return false
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {}
    }
}