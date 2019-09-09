package co.omise.android.ui

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.Capability
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaymentCreatorActivityTest {

    private lateinit var scenario: ActivityScenario<PaymentCreatorActivity>
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
        scenario = ActivityScenario.launch(intent)
        onView(withText(R.string.payment_chooser_title)).check(matches(isDisplayed()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun initialActivity_missExtrasIntent() {
        val noExtrasIntent = Intent(
                ApplicationProvider.getApplicationContext(),
                PaymentCreatorActivity::class.java
        )
        scenario = ActivityScenario.launch(noExtrasIntent)
    }
}
