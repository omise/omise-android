package co.omise.android.ui

import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.ui.CreditCardActivity.Companion.EXTRA_PKEY
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class CreditCardActivityTest {


    private lateinit var scenario: ActivityScenario<CreditCardActivity>
    private val intent = Intent(getApplicationContext(), CreditCardActivity::class.java).apply {
        putExtra(EXTRA_PKEY, "test_key1234")
    }

    @Before
    fun setUp() {
        scenario = launch(intent)
    }

    @Test
    fun startActivity() {
        onView(withId(R.id.button_submit)).check(matches(isDisplayed()))
    }

    @Test
    fun submitForm_authenticationError() {
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.spinner_expiry_month)).perform(click())
        onData(allOf(`is`(instanceOf(Integer::class.java)), `is`(4))).perform(click())
        onView(withId(R.id.spinner_expiry_year)).perform(click())
        onData(allOf(`is`(instanceOf(Integer::class.java)), `is`(2020))).perform(click())
        onView(withId(R.id.edit_security_code)).perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.button_submit)).perform(click())

        sleep(3000)
        onView(withId(R.id.text_error_message)).check(matches(withText("Error: authentication failed")))
    }

    @Test
    fun backPressed_setResultCanceled() {
        pressBackUnconditionally()
        val result = scenario.result
        assertEquals(RESULT_CANCELED, result.resultCode)
    }
}
