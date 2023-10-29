package co.omise.android.ui

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExpiryDateEditTextTest {

    private val editText = ExpiryDateEditText(ApplicationProvider.getApplicationContext())

    @Test
    fun format_splitMonthAndYear() {
        "1234".forEach { editText.append(it.toString()) }

        assertEquals("12/34", editText.text.toString())
        assertEquals(12, editText.expiryMonth)
        assertEquals(2034, editText.expiryYear)
    }

    @Test
    fun format_putZeroPrefix() {
        "2".forEach { editText.append(it.toString()) }

        assertEquals("02/", editText.text.toString())
        assertEquals(2, editText.expiryMonth)
        assertEquals(0, editText.expiryYear)
    }

    @Test
    fun format_deleteYearValue() {
        "1234".forEach { editText.append(it.toString()) } // 12/34

        editText.text!!.delete(4, 5)
        editText.text!!.delete(3, 4)

        assertEquals("12/", editText.text.toString())
        assertEquals(12, editText.expiryMonth)
        assertEquals(0, editText.expiryYear)
    }

    @Test
    fun validate_validateValue() {
        "1234".forEach { editText.append(it.toString()) } // 12/34

        assertEquals(Unit, editText.validate())
    }

    @Test
    fun validate_ignoreSpecialCharactersInput() {
        "*,.".forEach { editText.append(it.toString()) }

        assertEquals("", editText.text.toString())
        "1234".forEach { editText.append(it.toString()) } // 12/34
        assertEquals("12/34", editText.text.toString())
    }

    @Test(expected = InputValidationException.EmptyInputException::class)
    fun validate_emptyValue() {
        "".forEach { editText.append(it.toString()) }

        editText.validate()
    }

    @Test(expected = InputValidationException.InvalidInputException::class)
    fun validate_invalidateWithIncompleteYearValue() {
        "123".forEach { editText.append(it.toString()) } // 12/03

        editText.validate()
    }

    @Test
    fun setExpiryDate_validArgument() {
        editText.setExpiryDate(12, 20)

        assertEquals("12/20", editText.text.toString())
        assertEquals(12, editText.expiryMonth)
        assertEquals(2020, editText.expiryYear)
    }

    @Test(expected = IllegalArgumentException::class)
    fun setExpiryDate_invalidArgument() {
        editText.setExpiryDate(0, 100)
    }
}