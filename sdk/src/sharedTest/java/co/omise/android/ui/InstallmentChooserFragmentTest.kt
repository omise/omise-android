package co.omise.android.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.PaymentMethod
import co.omise.android.utils.itemCount
import co.omise.android.utils.withListId
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstallmentChooserFragmentTest {

    private val paymentMethods = listOf(
            PaymentMethod(name = "installment_bay"),
            PaymentMethod(name = "installment_bbl"),
            PaymentMethod(name = "installment_ezypay"),
            PaymentMethod(name = "installment_first_choice"),
            PaymentMethod(name = "installment_kbank"),
            PaymentMethod(name = "installment_ktc"),
            PaymentMethod(name = "installment_scb"),
            PaymentMethod(name = "installment_citi"),
            PaymentMethod(name = "installment_ttb"),
            PaymentMethod(name = "installment_uob")
    )
    private val mockNavigation: PaymentCreatorNavigation = mock()
    private val fragment = InstallmentChooserFragment.newInstance(paymentMethods).apply {
        navigation = mockNavigation
    }

    @Before
    fun setUp() {
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity { it.replaceFragment(fragment) }
        onView(withText(R.string.installments_title)).check(matches(isDisplayed()))
    }

    @Test
    fun displayAllowedInstallmentBanks_showAllowedInstallmentBanksFromArgument() {
        onView(withListId(R.id.recycler_view).atPosition(0)).check(matches(hasDescendant(withText(R.string.payment_method_installment_bay_title))))
        onView(withListId(R.id.recycler_view).atPosition(1)).check(matches(hasDescendant(withText(R.string.payment_method_installment_bbl_title))))
        onView(withListId(R.id.recycler_view).atPosition(2)).check(matches(hasDescendant(withText(R.string.payment_method_installment_ezypay_title))))
        onView(withListId(R.id.recycler_view).atPosition(3)).check(matches(hasDescendant(withText(R.string.payment_method_installment_first_choice_title))))
        onView(withListId(R.id.recycler_view).atPosition(4)).check(matches(hasDescendant(withText(R.string.payment_method_installment_kasikorn_title))))
        onView(withListId(R.id.recycler_view).atPosition(5)).check(matches(hasDescendant(withText(R.string.payment_method_installment_ktc_title))))
        onView(withListId(R.id.recycler_view).atPosition(6)).check(matches(hasDescendant(withText(R.string.payment_method_installment_scb_title))))

        onView(withId(R.id.recycler_view)).perform(ViewActions.swipeUp());

        onView(withListId(R.id.recycler_view).atPosition(7)).check(matches(hasDescendant(withText(R.string.payment_method_installment_citi_title))))
        onView(withListId(R.id.recycler_view).atPosition(8)).check(matches(hasDescendant(withText(R.string.payment_method_installment_ttb_title))))
        onView(withListId(R.id.recycler_view).atPosition(9)).check(matches(hasDescendant(withText(R.string.payment_method_installment_uob_title))))

        onView(withId(R.id.recycler_view)).check(matches(itemCount(paymentMethods.size)))
    }

    @Test
    fun clickBankInstallmentMethod_shouldNavigateToInstallmentTermChooserFragment() {
        onView(withListId(R.id.recycler_view).atPosition(0)).perform(click())
        verify(mockNavigation).navigateToInstallmentTermChooser(PaymentMethod(name = "installment_bay"))
    }
}
