package co.omise.android.ui

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.omise.android.R
import co.omise.android.utils.focus
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreditCardActivityInstrumentedTest {
    private lateinit var scenario: ActivityScenario<CreditCardActivity>
    private val intent = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
        putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
    }

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(intent)
    }

    @Test
    fun errorMessages_showErrorMessage() {
        onView(withId(R.id.edit_card_number)).perform(typeText("42424242"), pressImeActionButton())

        onView(withId(R.id.text_card_number_error)).check(matches(withText(R.string.error_invalid_card_number)))


        onView(withId(R.id.edit_expiry_date)).perform(typeText("123"), pressImeActionButton())

        onView(withId(R.id.text_expiry_date_error)).check(matches(withText(R.string.error_invalid_expiry_date)))


        onView(withId(R.id.edit_security_code)).perform(typeText("12"))
        onView(withId(R.id.edit_expiry_date)).perform(focus())

        onView(withId(R.id.text_security_code_error)).check(matches(withText(R.string.error_invalid_security_code)))
    }
}