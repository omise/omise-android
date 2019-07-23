package co.omise.android.ui

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardNameEditTextTest {
    private val editText = OmiseEditText(ApplicationProvider.getApplicationContext())

    @Test(expected = InputValidationException.EmptyInputException::class)
    fun validate_emptyValue() {
        editText.validate()
    }
}
