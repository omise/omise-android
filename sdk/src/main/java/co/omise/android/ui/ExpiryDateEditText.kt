package co.omise.android.ui

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import java.util.Calendar
import java.util.GregorianCalendar


class ExpiryDateEditText : OmiseEditText {

    private var cursorPosition = 0

    private var textWatcher = ExpiryDateTextWatcher()
    private var textListener: ExpiryDateChangeListener? = null
    private val startedYear: Int by lazy {
        val currentYear = GregorianCalendar.getInstance().get(Calendar.YEAR)
        currentYear - (currentYear % YEAR_LAST_TWO_DIGIT_MOD)
    }

    var expiryMonth: Int = 0
    var expiryYear: Int = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addTextChangedListener(textWatcher)
        disableOptions()
        filters = arrayOf(InputFilter.LengthFilter(MAX_CHARS))
        inputType = InputType.TYPE_CLASS_PHONE
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)

        text?.let { setSelection(it.length) }
    }

    private inner class ExpiryDateTextWatcher : TextWatcher {
        var beforeChangedText: String = ""
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            text?.let { cursorPosition = it.length - selectionStart }
            beforeChangedText = s.toString()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s == null || s.length > MAX_CHARS) return

            // On deleting
            if (s.length < beforeChangedText.length) {
                if (beforeChangedText[beforeChangedText.length - 1].toString() == DATE_SEPARATOR) {
                    val afterDeletedText = s.substring(0, s.length - 1)
                    setText(afterDeletedText)
                    notifyExpiryDateChanged(afterDeletedText)
                } else {
                    notifyExpiryDateChanged(s.toString())
                }
                return
            }

            setExpiryDateText(s.toString())
            notifyExpiryDateChanged(text.toString())
        }
    }

    private fun setExpiryDateText(dateString: String) {
        removeTextChangedListener(textWatcher)

        val formattedString = formatString(dateString)
        setText(formattedString)
        setSelection(text.toString().length - cursorPosition)

        addTextChangedListener(textWatcher)
    }

    private fun notifyExpiryDateChanged(formattedString: String = "") {
        if (formattedString.isNullOrEmpty()) {
            expiryMonth = 0
            expiryYear = 0
            textListener?.textFormatted(null, null)
            return
        }

        val (month, year) = formattedString.separateDates()

        expiryMonth = month ?: 0
        expiryYear = year?.plus(startedYear) ?: 0

        textListener?.textFormatted(expiryMonth, expiryYear)
    }

    // Have to add this internal listener because if we add an external TextWatcher to this custom EditText
    // the first character would be set twice
    fun setInternalTextChangedListener(textListener: ExpiryDateChangeListener) {
        this.textListener = textListener
    }

    fun setExpiryDate(month: Int, year: Int) {
        TODO("Set expiry date.")
    }

    companion object {
        private const val DATE_SEPARATOR = "/"
        private const val MAX_CHARS = 5 // Included separator
        private const val MAX_MONTH = 12
        private const val YEAR_LAST_TWO_DIGIT_MOD = 100
    }

    interface ExpiryDateChangeListener {
        fun textFormatted(month: Int?, year: Int?)
    }

    private fun formatString(str: String = ""): String {
        return when {
            str.length == 1 && str.toInt() > 1 -> str.addZeroPrefixIfNeed()
            str.length == 2 && str.toInt() > MAX_MONTH -> MAX_MONTH.toString()
            else -> str
        }.addDateSeparatorIfNeed()
    }

    private fun String.addDateSeparatorIfNeed(): String {
        if (this.length != 2 || this.contains(DATE_SEPARATOR)) return this
        return this + DATE_SEPARATOR
    }

    private fun String.addZeroPrefixIfNeed(): String {
        return if (this.toInt() > 1) {
            "0$this"
        } else {
            this
        }
    }

    private fun String.separateDates(): Pair<Int?, Int?> {
        if (!this.isNullOrEmpty() && !this.contains(DATE_SEPARATOR)) return Pair(this.toIntOrNull(), null)

        val dates = this.split(DATE_SEPARATOR.toRegex())

        return if (dates.size > 1) {
            Pair(dates[0].toIntOrNull(), dates[1].toIntOrNull())
        } else {
            Pair(dates[0].toIntOrNull(), null)
        }
    }
}
