package co.omise.android.ui

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.Capability
import co.omise.android.models.PaymentMethod
import co.omise.android.utils.itemCount
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PaymentChooserFragmentTest {
    private lateinit var scenario: ActivityScenario<TestFragmentActivity>
    private lateinit var fragment: PaymentChooserFragment
    private val mockNavigation: PaymentCreatorNavigation = mock()

    @get:Rule
    val intentRule = IntentsTestRule<TestFragmentActivity>(TestFragmentActivity::class.java)

    @Before
    fun setUp() {
        val paymentMethods = listOf(
                PaymentMethod(name = "card"),
                PaymentMethod(name = "installment_bay"),
                PaymentMethod(name = "installment_bbl"),
                PaymentMethod(name = "installment_first_choice"),
                PaymentMethod(name = "installment_kbank"),
                PaymentMethod(name = "installment_ktc"),
                PaymentMethod(name = "internet_banking_bay"),
                PaymentMethod(name = "internet_banking_bbl"),
                PaymentMethod(name = "internet_banking_ktb"),
                PaymentMethod(name = "internet_banking_scb"),
                PaymentMethod(name = "bill_payment_tesco_lotus"),
                PaymentMethod(name = "econtext"),
                PaymentMethod(name = "alipay")
        )
        val capability = Capability(
                paymentMethods = paymentMethods
        )

        fragment = PaymentChooserFragment.newInstance(capability).apply {
            navigation = mockNavigation
        }

        intending(hasComponent(hasClassName(TestFragmentActivity::class.java.name)))
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, Intent()))

        scenario = ActivityScenario.launch(TestFragmentActivity::class.java).onActivity {
            it.startActivityForResult(Intent(it, TestFragmentActivity::class.java), 0)
            it.replaceFragment(fragment)
        }
    }

    @Test
    fun displayPaymentMethods_showPaymentMethodsFromCapability() {
        onView(withText(R.string.payment_chooser_title)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_view)).check(matches(itemCount(8)))
    }

    @Test
    fun closeMenu_finishActivityWithCanceledCode() {
        onView(withId(R.id.close_menu)).perform(click())

        scenario.onActivity {
            assertEquals(Activity.RESULT_CANCELED, it.activityResult?.resultCode)
        }
    }

    @Test
    fun clickCreditCardPaymentMethod_navigateToCreditCardFrom() {
        onView(withId(R.id.recycler_view))
                .perform(actionOnItemAtPosition<OmiseItemViewHolder>(0, click()))

        verify(fragment.navigation)?.navigateToCreditCardForm()
    }

    @Test
    fun clickInternetBankingPaymentMethod_navigateToInternetBankingChooser() {
        onView(withId(R.id.recycler_view))
                .perform(actionOnItemAtPosition<OmiseItemViewHolder>(2, click()))

        val expectedMethods = listOf(
                PaymentMethod(name = "internet_banking_bay"),
                PaymentMethod(name = "internet_banking_bbl"),
                PaymentMethod(name = "internet_banking_ktb"),
                PaymentMethod(name = "internet_banking_scb")
        )
        verify(fragment.navigation)?.navigateToInternetBankingChooser(expectedMethods)
    }

    @Test
    fun clickInstallmentPaymentMethod_navigateToInstallmentChooser() {
        onView(withId(R.id.recycler_view))
                .perform(actionOnItemAtPosition<OmiseItemViewHolder>(1, click()))

        val expectedMethods = listOf(
                PaymentMethod(name = "installment_bay"),
                PaymentMethod(name = "installment_bbl"),
                PaymentMethod(name = "installment_first_choice"),
                PaymentMethod(name = "installment_kbank"),
                PaymentMethod(name = "installment_ktc")
        )
        verify(fragment.navigation)?.navigateToInstallmentChooser(expectedMethods)
    }
}
