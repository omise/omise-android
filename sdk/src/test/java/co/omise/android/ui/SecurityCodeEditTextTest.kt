package co.omise.android.ui

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurityCodeEditTextTest {

    private val editText = SecurityCodeEditText(ApplicationProvider.getApplicationContext())

    @Test
    fun validate_validValue() {
        "123".forEach { editText.append(it.toString()) }

        assertEquals(Unit, editText.validate())
    }

    @Test(expected = InputValidationException.InvalidInputException::class)
    fun validate_invalidValue() {
        "12".forEach { editText.append(it.toString()) }

        editText.validate()
    }
}
