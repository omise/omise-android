package co.omise.android.ui

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity

@RunWith(AndroidJUnit4::class)
class CreditCardEditTextTest {

    private val activityController = buildActivity(Activity::class.java)
    private val editText = CreditCardEditText(activityController.get())

    @Test
    fun typeCardNumber_formatCardNumber() {
        "4242424242424242".forEach { editText.append(it.toString()) }

        assertEquals("4242 4242 4242 4242", editText.text.toString())
    }

    @Test
    fun format_firstGroup() {
        "42424".forEach { editText.append(it.toString()) }

        assertEquals("4242 4", editText.text.toString())
    }

    @Test
    fun delete_collectPosition() {
        "42424".forEach { editText.append(it.toString()) } // 4242 4

        editText.text = editText.text?.delete(5, 6)
        editText.text = editText.text?.delete(3, 4)

        assertEquals("424", editText.text.toString())
    }

    @Test
    fun typeCardNumber_maxCardNumberLength() {
        "42424242424242424242424".forEach { editText.append(it.toString()) }

        assertEquals("4242 4242 4242 4242", editText.text.toString())
    }

    @Test
    fun validate_validValue() {
        "4242424242424242".forEach { editText.append(it.toString()) }

        assertEquals(Unit, editText.validate())
    }

    @Test(expected = InputValidationException.EmptyInputException::class)
    fun validate_emptyValue() {
        "".forEach { editText.append(it.toString()) }

        editText.validate()
    }

    @Test(expected = InputValidationException.InvalidInputException::class)
    fun validate_invalidNumber() {
        "1234567890123456".forEach { editText.append(it.toString()) }

        editText.validate()
    }
}
