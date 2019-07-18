package co.omise.android.ui

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity

@RunWith(AndroidJUnit4::class)
class OmiseEditTextTest {

    private val activityCollection = buildActivity(Activity::class.java)
    private val editText = TestOmiseEditText(activityCollection.get())

    @Test
    fun errorMessage_setErrorMessage() {
        editText.errorMessage = "Invalid input."
        assertEquals("Invalid input.", editText.errorMessage)
    }

    @Test
    fun errorMessage_clearErrorMessage() {
        editText.errorMessage = "Invalid input."
        editText.errorMessage = null
        assertNull(editText.errorMessage)
    }
}

private class TestOmiseEditText : OmiseEditText {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}
