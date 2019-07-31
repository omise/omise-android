package co.omise.android.ui

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OmiseEditTextTest {

    private val editText = OmiseEditText(ApplicationProvider.getApplicationContext())

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

    @Test(expected = InputValidationException.EmptyInputException::class)
    fun validate_emptyValue() {
        editText.validate()
    }
}
