package co.omise.android.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.PaymentMethod
import co.omise.android.utils.itemCount
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InternetBankingChooserFragmentTest {

    @Test
    fun displayAvailableBanks() {
        val paymentMethods = listOf(
                PaymentMethod(name = "internet_banking_bbl"),
                PaymentMethod(name = "internet_banking_scb"),
                PaymentMethod(name = "internet_banking_bay"),
                PaymentMethod(name = "internet_banking_ktb")
        )
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity {
            val fragment = InternetBankingChooserFragment.newInstance(paymentMethods)
            it.replaceFragment(fragment)
        }

        onView(withText("Internet Banking")).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_view)).check(matches(itemCount(paymentMethods.size)))
    }
}