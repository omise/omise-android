package co.omise.android.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.PaymentMethod
import co.omise.android.utils.itemCount
import co.omise.android.utils.withListId
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class InstallmentChooserFragmentTest {
    private val paymentMethods =
        listOf(
            PaymentMethod(name = "installment_bay"),
            PaymentMethod(name = "installment_bbl"),
            PaymentMethod(name = "installment_mbb"),
            PaymentMethod(name = "installment_first_choice"),
            PaymentMethod(name = "installment_kbank"),
            PaymentMethod(name = "installment_ktc"),
            PaymentMethod(name = "installment_scb"),
            PaymentMethod(name = "installment_ttb"),
            PaymentMethod(name = "installment_uob"),
        )
    private val mockNavigation: PaymentCreatorNavigation = mock()
    private var fragment =
        InstallmentChooserFragment.newInstance(paymentMethods, 300000, 200000).apply {
            navigation = mockNavigation
        }

    @Test
    fun displayAllowedInstallmentBanks_showAllowedInstallmentBanksFromArgument() {
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity { it.replaceFragment(fragment) }
        onView(withText(R.string.installments_title)).check(matches(isDisplayed()))

        onView(
            withListId(R.id.recycler_view).atPosition(0),
        ).check(matches(isEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_bay_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(1),
        ).check(matches(isEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_bbl_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(2),
        ).check(matches(isEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_mbb_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(3),
        ).check(matches(isEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_first_choice_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(4),
        ).check(matches(isEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_kasikorn_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(5),
        ).check(matches(isEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_ktc_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(6),
        ).check(matches(isEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_scb_title))))

        onView(withId(R.id.recycler_view)).perform(ViewActions.swipeUp())

        onView(
            withListId(R.id.recycler_view).atPosition(7),
        ).check(matches(isEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_ttb_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(8),
        ).check(matches(isEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_uob_title))))

        onView(withId(R.id.recycler_view)).check(matches(itemCount(paymentMethods.size)))
    }

    @Test
    fun clickBankInstallmentMethod_shouldNavigateToInstallmentTermChooserFragment() {
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity { it.replaceFragment(fragment) }

        onView(withListId(R.id.recycler_view).atPosition(0)).perform(click())
        verify(mockNavigation).navigateToInstallmentTermChooser(PaymentMethod(name = "installment_bay"))
    }

    @Test
    fun displayAllowedInstallmentBanks_showAllowedInstallmentBanksAsDisabledFromArgument() {
        fragment =
            InstallmentChooserFragment.newInstance(paymentMethods, 100000, 200000).apply {
                navigation = mockNavigation
            }
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity { it.replaceFragment(fragment) }
        onView(
            withListId(R.id.recycler_view).atPosition(0),
        ).check(matches(isNotEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_bay_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(1),
        ).check(matches(isNotEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_bbl_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(2),
        ).check(matches(isNotEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_mbb_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(3),
        ).check(matches(isNotEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_first_choice_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(4),
        ).check(matches(isNotEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_kasikorn_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(5),
        ).check(matches(isNotEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_ktc_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(6),
        ).check(matches(isNotEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_scb_title))))

        onView(withId(R.id.recycler_view)).perform(ViewActions.swipeUp())

        onView(
            withListId(R.id.recycler_view).atPosition(7),
        ).check(matches(isNotEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_ttb_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(8),
        ).check(matches(isNotEnabled()))
            .check(matches(hasDescendant(withText(R.string.payment_method_installment_uob_title))))

        onView(withId(R.id.recycler_view)).check(matches(itemCount(paymentMethods.size)))
    }

    @Test
    fun clickBankInstallmentMethod_shouldNotNavigateToInstallmentTermChooserFragment() {
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity { it.replaceFragment(fragment) }

        onView(withListId(R.id.recycler_view).atPosition(0)).perform(click())
        onView(withText(R.string.installments_title)).check(matches(isDisplayed()))
    }
}
