package co.omise.android.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.Source
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify


@RunWith(AndroidJUnit4::class)
class TrueMoneyFormFragmentTest {

    private val mockRequester: PaymentCreatorRequester<Source> = mock {
        on { amount }.doReturn(40000L)
        on { currency }.doReturn("thb")
    }

    private val fragment = TrueMoneyFormFragment().apply {
        requester = mockRequester
    }

    @Before
    fun setUp() {
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity {
            it.replaceFragment(fragment)
        }

        onView(withText(R.string.payment_truemoney_title)).check(matches(isDisplayed()))
    }

    @Test
    fun clickSubmitButton_requestCreatingSource() {
        onView(withId(R.id.edit_phone_number)).perform(typeText("0812345678"), pressImeActionButton())

        onView(withId(R.id.button_submit)).perform(click())

        verify(mockRequester).request(any(), any())
    }

    @Test
    fun validInputs_enableSubmitButton() {
        onView(withId(R.id.edit_phone_number)).perform(typeText("0812345678"), pressImeActionButton())

        onView(withId(R.id.button_submit)).check(matches(isEnabled()))
    }

    @Test
    fun invalidInputs_disableSubmitButton() {
        onView(withId(R.id.edit_phone_number)).perform(typeText(""), pressImeActionButton())

        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun disableForm_disableFormWhenRequestSent() {
        onView(withId(R.id.edit_phone_number)).perform(typeText("0812345678"), pressImeActionButton())

        onView(withId(R.id.button_submit)).perform(click())

        onView(withId(R.id.edit_phone_number)).check(matches(not(isEnabled())))
        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }
}