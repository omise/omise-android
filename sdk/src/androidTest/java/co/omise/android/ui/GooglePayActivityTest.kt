package co.omise.android.ui

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launchActivityForResult
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.omise.android.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GooglePayActivityTest {
    private lateinit var scenario: ActivityScenario<GooglePayActivity>
    private val intent =
        Intent(InstrumentationRegistry.getInstrumentation().context, GooglePayActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
            putExtra(OmiseActivity.EXTRA_CARD_BRANDS, arrayListOf("JCB"))
            putExtra(OmiseActivity.EXTRA_AMOUNT, 2000)
            putExtra(OmiseActivity.EXTRA_CURRENCY, "THB")
            putExtra(OmiseActivity.EXTRA_GOOGLEPAY_MERCHANT_ID, "testId")
            putExtra(OmiseActivity.EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS, "address")
            putExtra(OmiseActivity.EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER, "number")
        }
    private val application = (InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application)
    private val activityLifecycleCallbacks =
        object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(
                activity: Activity,
                savedInstanceState: Bundle?,
            ) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(
                activity: Activity,
                outState: Bundle,
            ) {}

            override fun onActivityDestroyed(activity: Activity) {}
        }

    @Before
    fun setUp() {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        scenario = launchActivityForResult(intent)
    }

    @After
    fun tearDown() {
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    @Test
    fun check_google_pay_button_exists() {
        onView(withId(R.id.googlePayButton)).check(matches(isDisplayed()))
        onView(withId(R.id.googlePayButton)).perform(click())
    }
}
