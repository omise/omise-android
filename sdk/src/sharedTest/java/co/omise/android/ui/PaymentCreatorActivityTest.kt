package co.omise.android.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.WindowManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.Capability
import co.omise.android.models.Token
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_TOKEN
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaymentCreatorActivityTest {

    @get:Rule
    val intentRule = IntentsRule()

    private val capability = Capability()
    private val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PaymentCreatorActivity::class.java
    ).apply {
        putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
        putExtra(OmiseActivity.EXTRA_AMOUNT, 50000)
        putExtra(OmiseActivity.EXTRA_CURRENCY, "thb")
        putExtra(OmiseActivity.EXTRA_CAPABILITY, capability)
    }

    @Test
    fun initialActivity_collectExtrasIntent() {
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent)

        onView(withId(R.id.payment_creator_container)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToCreditCardForm_startCreditCartActivity() {
        var activity: PaymentCreatorActivity? = null
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        activity?.navigation?.navigateToCreditCardForm()

        intended(hasComponent(hasClassName(CreditCardActivity::class.java.name)))
    }

    @Test
    fun creditCardResult_resultOk() {
        val creditCardIntent = Intent().apply {
            putExtra(EXTRA_TOKEN, Token())
        }
        val scenario = ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            it.performActivityResult(100, RESULT_OK, creditCardIntent)
        }

        assertEquals(RESULT_OK, scenario.result.resultCode)
    }

    @Test
    fun flagSecure_activityShouldContainFlagSecureInAttributes() {
        val scenario = ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent)
        scenario.onActivity {
            assertEquals(WindowManager.LayoutParams.FLAG_SECURE, it.window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
