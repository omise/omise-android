package co.omise.android.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.Bank
import co.omise.android.models.Capability
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FpxEmailFormFragmentTest {

    private val banks = listOf(Bank("affin", "Affin Bank", true))

    private val paymentMethods = mutableListOf(PaymentMethod(
            name = "fpx",
            currencies = arrayListOf("MYR"),
            banks = banks
    ))

    private val mockNavigation: PaymentCreatorNavigation = mock()

    private val mockRequester: PaymentCreatorRequester<Source> = mock {
        on { capability }.doReturn(Capability(paymentMethods = paymentMethods))
    }

    private val fragment = FpxEmailFormFragment().apply {
        requester = mockRequester
        navigation = mockNavigation
    }

    @Before
    fun setup() {
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity { it.replaceFragment(fragment) }

        onView(withText(R.string.payment_method_fpx_title)).check(matches(isDisplayed()))
    }

    @Test
    fun clickSubmitButton_requestNavigateToBankChooser() {
        onView(withId(R.id.edit_email)).perform(typeText("example@omise.co"), pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(click())

        verify(mockNavigation).navigateToFpxBankChooser(banks, "example@omise.co")
    }

    @Test
    fun validInputs_enableSubmitButton() {
        onView(withId(R.id.edit_email)).perform(typeText("example@omise.co"), pressImeActionButton())
        onView(withId(R.id.button_submit)).check(matches(isEnabled()))

        onView(withId(R.id.edit_email)).perform(typeText(""), pressImeActionButton())
        onView(withId(R.id.button_submit)).check(matches(isEnabled()))
    }

    @Test
    fun invalidInputs_disableSubmitButton() {
        onView(withId(R.id.edit_email)).perform(typeText("example@"), pressImeActionButton())
        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }
}
