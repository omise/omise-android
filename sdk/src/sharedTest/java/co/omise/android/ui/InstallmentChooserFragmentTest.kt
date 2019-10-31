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
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstallmentChooserFragmentTest {

    private val paymentMethods = listOf(
            PaymentMethod(name = "installment_bay"),
            PaymentMethod(name = "installment_bbl"),
            PaymentMethod(name = "installment_first_choice"),
            PaymentMethod(name = "installment_kbank"),
            PaymentMethod(name = "installment_ktc")
    )
    private val mockNavigation: PaymentCreatorNavigation = mock()
    private val fragment = InstallmentChooserFragment.newInstance(paymentMethods).apply {
        navigation = mockNavigation
    }

    @Before
    fun setUp() {
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity {
            it.replaceFragment(fragment)
        }

        onView(withText(R.string.installments_title)).check(matches(isDisplayed()))
    }

    @Test
    fun displayAllowedInstallmentBanks_showAllowedInstallmentBanksFromArgument() {
        onView(withId(R.id.recycler_view)).check(matches(itemCount(paymentMethods.size)))
    }
}
