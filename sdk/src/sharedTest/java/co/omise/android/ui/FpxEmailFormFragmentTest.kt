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
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FpxEmailFormFragmentTest {

    private val banks = listOf(Bank("affin", "Affin Bank", true))

    private val paymentMethods = listOf(PaymentMethod(
            name = "fpx",
            currencies = arrayListOf("MYR"),
            banks = banks
    ))

    private val mockNavigation: PaymentCreatorNavigation = mock()

    private val mockRequester: PaymentCreatorRequester<Source> = mock {
        on { capability }.doReturn(Capability(paymentMethods = paymentMethods))
        on { specificPaymentMode }.doReturn(false)
    }

    private val fragment = FpxEmailFormFragment().apply {
        requester = mockRequester
        navigation = mockNavigation
    }

    private lateinit var scenario: ActivityScenario<TestFragmentActivity>

    @After
    fun cleanup() {
        scenario.close()
    }

    @Test
    fun clickSubmitButton_requestNavigateToBankChooserWithCapabilityMode() {
        scenario = ActivityScenario.launch(TestFragmentActivity::class.java).onActivity { it.replaceFragment(fragment) }

        onView(withText(R.string.payment_method_fpx_title)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_email)).perform(typeText("example@omise.co"), pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(click())

        verify(mockNavigation).navigateToFpxBankChooser(banks, "example@omise.co")
    }

    @Test
    fun clickSubmitButton_requestNavigateToBankChooserWithSpecificPaymentMode() {
        scenario = ActivityScenario.launch(TestFragmentActivity::class.java).onActivity {
            it.replaceFragment(FpxEmailFormFragment().apply {
                requester = mock {
                    on { specificPaymentMode }.doReturn(true)
                }
                navigation = mockNavigation
            })
        }

        onView(withText(R.string.payment_method_fpx_title)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_email)).perform(typeText("example@omise.co"), pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(click())

        verify(mockNavigation).navigateToFpxBankChooser(listOf(
                Bank("AmBank", "ambank", true),
                Bank("OCBC Bank", "ocbc", false)
        ), "example@omise.co")
    }

    @Test
    fun validInputs_enableSubmitButton() {
        scenario = ActivityScenario.launch(TestFragmentActivity::class.java).onActivity { it.replaceFragment(fragment) }

        onView(withText(R.string.payment_method_fpx_title)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_email)).perform(typeText("example@omise.co"), pressImeActionButton())
        onView(withId(R.id.button_submit)).check(matches(isEnabled()))

        onView(withId(R.id.edit_email)).perform(typeText(""), pressImeActionButton())
        onView(withId(R.id.button_submit)).check(matches(isEnabled()))
    }

    @Test
    fun invalidInputs_disableSubmitButton() {
        scenario = ActivityScenario.launch(TestFragmentActivity::class.java).onActivity { it.replaceFragment(fragment) }

        onView(withText(R.string.payment_method_fpx_title)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_email)).perform(typeText("example@"), pressImeActionButton())
        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }
}
