package co.omise.android.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.Bank
import co.omise.android.models.Capability
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class FpxEmailFormFragmentTest {
    private val banks = listOf(Bank("affin", "Affin Bank", true))

    private val paymentMethods =
        mutableListOf(
            PaymentMethod(
                name = "fpx",
                currencies = arrayListOf("MYR"),
                banks = banks,
            ),
        )

    private val mockNavigation: PaymentCreatorNavigation = mock()

    private val mockRequester: PaymentCreatorRequester<Source> =
        mock {
            on { capability }.doReturn(Capability(paymentMethods = paymentMethods))
        }

    private val fragment =
        FpxEmailFormFragment().apply {
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
        onView(withId(R.id.button_submit)).perform(scrollTo(), click())

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
