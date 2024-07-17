package co.omise.android.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
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
import co.omise.android.models.Capability
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class AtomeFormFragmentTest {
    private val capability = Capability.create(sourceTypes = listOf(SourceType.Atome))

    private val mockRequester: PaymentCreatorRequester<Source> =
        mock {
            on { capability }.doReturn(capability)
            on { currency }.doReturn("TH")
            on { amount }.doReturn(1000)
        }

    private val fragment =
        AtomeFormFragment().apply {
            requester = mockRequester
        }

    @Before
    fun setup() {
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity { it.replaceFragment(fragment) }

        onView(withText(R.string.atome_info_text)).check(matches(isDisplayed()))
    }

    @Test
    fun validInputs_enableSubmitButton() {
        // fill required fields
        onView(withId(R.id.edit_phone_number)).perform(scrollTo(), typeText("000"), pressImeActionButton())
        // The submit button should not be enabled unless all required fields are filled
        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_shipping_street)).perform(scrollTo(), typeText("test"), pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_shipping_postal)).perform(scrollTo(), typeText("test"), pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_shipping_city)).perform(scrollTo()).perform(typeText("test"), pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_shipping_country)).perform(scrollTo()).perform(typeText("TH"), pressImeActionButton())
        // Submit button should be enabled as all required fields have been filled
        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(isEnabled()))
        // fill non required fields
        onView(withId(R.id.edit_full_name)).perform(scrollTo(), typeText("test"), pressImeActionButton())
        onView(withId(R.id.edit_email)).perform(scrollTo(), typeText("test@gmail.com"), pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(isEnabled()))
        // optional invalid email should disable submit button after all required fields are filled
        onView(withId(R.id.edit_email)).perform(scrollTo(), clearText()).perform(typeText("test")).perform(pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_full_name)).perform(scrollTo(), typeText("test")).perform(pressImeActionButton())

        onView(withId(R.id.edit_email)).perform(scrollTo(), clearText()).perform(typeText("test@gmail.com")).perform(pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(isEnabled())).perform(
            click(),
        )
    }

    @Test
    fun invalidInputs_disableSubmitButton() {
        onView(withId(R.id.edit_phone_number)).perform(scrollTo(), click()).perform(pressImeActionButton())
        onView(withId(R.id.edit_email)).perform(scrollTo(), typeText("example@"), pressImeActionButton())
        onView(withId(R.id.edit_full_name)).perform(scrollTo(), click()).perform(pressImeActionButton())
        onView(withId(R.id.edit_shipping_country)).perform(scrollTo()).perform(click()).perform(pressImeActionButton())
        onView(withId(R.id.edit_shipping_postal)).perform(scrollTo(), click()).perform(pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(not(isEnabled())))
    }

    @Test
    fun disable_checkBox_submitForm() {
        // Fill are required fields
        onView(withId(R.id.edit_phone_number)).perform(scrollTo(), typeText("000"), pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_shipping_street)).perform(scrollTo()).perform(typeText("test"), pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_shipping_postal)).perform(scrollTo(), typeText("test"), pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_shipping_city)).perform(scrollTo()).perform(typeText("test"), pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_shipping_country)).perform(scrollTo(), typeText("TH"), pressImeActionButton())
        // Uncheck address checkbox
        onView(withId(R.id.checkbox_billing_shipping)).perform(scrollTo(), click())
        onView(withId(R.id.edit_billing_street)).perform(scrollTo()).perform(typeText("test"), pressImeActionButton())
        onView(withId(R.id.edit_billing_postal)).perform(scrollTo()).perform(typeText("test"), pressImeActionButton())
        onView(withId(R.id.edit_billing_city)).perform(scrollTo()).perform(typeText("test"), pressImeActionButton())
        onView(withId(R.id.edit_billing_country)).perform(scrollTo()).perform(typeText("TH"), pressImeActionButton())

        onView(withId(R.id.button_submit)).perform(scrollTo()).check(matches(isEnabled())).perform(
            click(),
        )
    }
}
