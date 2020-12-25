package co.omise.android.ui

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.Capability
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.SupportedEcontext
import co.omise.android.utils.itemCount
import co.omise.android.utils.withListId
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PaymentChooserFragmentTest {
    private lateinit var scenario: ActivityScenario<TestFragmentActivity>
    private lateinit var fragment: PaymentChooserFragment
    private val mockNavigation: PaymentCreatorNavigation = mock()
    private val mockRequester: PaymentCreatorRequester<Source> = mock {
        on { amount }.doReturn(500000L)
        on { currency }.doReturn("thb")
    }

    @Before
    fun setUp() {
        Intents.init()

        val paymentMethods = listOf(
                PaymentMethod(name = "card"),
                PaymentMethod(name = "installment_bay"),
                PaymentMethod(name = "installment_bbl"),
                PaymentMethod(name = "installment_first_choice"),
                PaymentMethod(name = "installment_kbank"),
                PaymentMethod(name = "installment_ktc"),
                PaymentMethod(name = "installment_scb"),
                PaymentMethod(name = "internet_banking_bay"),
                PaymentMethod(name = "internet_banking_bbl"),
                PaymentMethod(name = "internet_banking_ktb"),
                PaymentMethod(name = "internet_banking_scb"),
                PaymentMethod(name = "bill_payment_tesco_lotus"),
                PaymentMethod(name = "econtext"),
                PaymentMethod(name = "alipay"),
                PaymentMethod(name = "mobile_banking_scb")
        )
        val capability = Capability(
                paymentMethods = paymentMethods
        )

        fragment = PaymentChooserFragment.newInstance(capability).apply {
            navigation = mockNavigation
            requester = mockRequester
        }

        intending(hasComponent(hasClassName(TestFragmentActivity::class.java.name)))
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, Intent()))

        scenario = ActivityScenario.launch(TestFragmentActivity::class.java).onActivity {
            it.startActivityForResult(Intent(it, TestFragmentActivity::class.java), 0)
            it.replaceFragment(fragment)
        }
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun displayPaymentMethods_showPaymentMethodsFromCapability() {
        onView(withText(R.string.payment_chooser_title)).check(matches(isDisplayed()))

        onView(withListId(R.id.recycler_view).atPosition(0)).check(matches(hasDescendant(withText(R.string.payment_method_credit_card_title))))
        onView(withListId(R.id.recycler_view).atPosition(1)).check(matches(hasDescendant(withText(R.string.payment_method_installments_title))))
        onView(withListId(R.id.recycler_view).atPosition(2)).check(matches(hasDescendant(withText(R.string.payment_method_internet_banking_title))))
        onView(withListId(R.id.recycler_view).atPosition(3)).check(matches(hasDescendant(withText(R.string.payment_method_tesco_lotus_title))))
        onView(withListId(R.id.recycler_view).atPosition(4)).check(matches(hasDescendant(withText(R.string.payment_method_convenience_store_title))))
        onView(withListId(R.id.recycler_view).atPosition(5)).check(matches(hasDescendant(withText(R.string.payment_method_pay_easy_title))))
        onView(withListId(R.id.recycler_view).atPosition(6)).check(matches(hasDescendant(withText(R.string.payment_method_netbank_title))))
        onView(withListId(R.id.recycler_view).atPosition(7)).check(matches(hasDescendant(withText(R.string.payment_method_alipay_title))))
        onView(withListId(R.id.recycler_view).atPosition(8)).check(matches(hasDescendant(withText(R.string.payment_method_mobile_banking_title))))


        onView(withId(R.id.recycler_view)).check(matches(itemCount(9)))
    }

    @Test
    fun closeMenu_finishActivityWithCanceledCode() {
        onView(withId(R.id.close_menu)).perform(click())

        assertEquals(Activity.RESULT_CANCELED, scenario.result.resultCode)
    }

    @Test
    fun clickCreditCardPaymentMethod_navigateToCreditCardFrom() {
        onView(withListId(R.id.recycler_view).atPosition(0)).perform(click())
        verify(fragment.navigation)?.navigateToCreditCardForm()
    }

    @Test
    fun clickInstallmentPaymentMethod_navigateToInstallmentChooser() {
        onView(withListId(R.id.recycler_view).atPosition(1)).perform(click())

        val expectedMethods = listOf(
                PaymentMethod(name = "installment_bay"),
                PaymentMethod(name = "installment_bbl"),
                PaymentMethod(name = "installment_first_choice"),
                PaymentMethod(name = "installment_kbank"),
                PaymentMethod(name = "installment_ktc"),
                PaymentMethod(name = "installment_scb")
        )
        verify(fragment.navigation)?.navigateToInstallmentChooser(expectedMethods)
    }

    @Test
    fun clickInternetBankingPaymentMethod_navigateToInternetBankingChooser() {
        onView(withListId(R.id.recycler_view).atPosition(2)).perform(click())

        val expectedMethods = listOf(
                PaymentMethod(name = "internet_banking_bay"),
                PaymentMethod(name = "internet_banking_bbl"),
                PaymentMethod(name = "internet_banking_ktb"),
                PaymentMethod(name = "internet_banking_scb")
        )
        verify(fragment.navigation)?.navigateToInternetBankingChooser(expectedMethods)
    }

    @Test
    fun clickBillPaymentTescoLotusPaymentMethod_sendRequestToCreateSource() {
        onView(withListId(R.id.recycler_view).atPosition(3)).perform(click())

        onView(withId(R.id.recycler_view)).check(matches(not(isEnabled())))
        verify(mockRequester).request(any(), any())
    }

    @Test
    fun clickConvenienceStoreMethod_sendRequestToCreateSource() {
        onView(withListId(R.id.recycler_view).atPosition(4)).perform(click())
        verify(fragment.navigation)?.navigateToEContextForm(SupportedEcontext.ConvenienceStore)
    }

    @Test
    fun clickPayEasyMethod_sendRequestToCreateSource() {
        onView(withListId(R.id.recycler_view).atPosition(5)).perform(click())
        verify(fragment.navigation)?.navigateToEContextForm(SupportedEcontext.PayEasy)
    }

    @Test
    fun clickNetBankingMethod_sendRequestToCreateSource() {
        onView(withListId(R.id.recycler_view).atPosition(6)).perform(click())
        verify(fragment.navigation)?.navigateToEContextForm(SupportedEcontext.Netbanking)
    }

    @Test
    fun clickAlipayPaymentMethod_sendRequestToCreateSource() {
        onView(withListId(R.id.recycler_view).atPosition(7)).perform(click())

        onView(withId(R.id.recycler_view)).check(matches(not(isEnabled())))
        verify(mockRequester).request(any(), any())
    }

    @Test
    fun clickMobileBankingPaymentMethod_navigateToMobileBankingChooser() {
        onView(withListId(R.id.recycler_view).atPosition(8)).perform(click())

        val expectedMethods = listOf(
                PaymentMethod(name = "mobile_banking_scb")
        )
        verify(fragment.navigation)?.navigateToMobileBankingChooser(expectedMethods)
    }
}
