package co.omise.android.ui

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
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
import co.omise.android.utils.itemCount
import co.omise.android.utils.withListId
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class InstallmentTermChooserFragmentTest {
    private var paymentMethod =
        PaymentMethod(
            name = "installment_bay",
            installmentTerms = listOf(3, 4, 6, 9, 10),
        )
    private var mockRequester: PaymentCreatorRequester<Source> =
        mock {
            on { amount }.doReturn(500000L)
            on { currency }.doReturn("thb")
            on { capability }.doReturn(
                Capability.create(sourceTypes = emptyList(), tokenizationMethods = emptyList(), zeroInterestInstallments = true),
            )
        }

    private var fragment =
        InstallmentTermChooserFragment.newInstance(paymentMethod).apply {
            requester = mockRequester
        }

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun displayAllowedInstallmentTerms_showAllowedInstallmentTermsFromArgument() {
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity {
            it.replaceFragment(fragment)
        }
        onView(withText(R.string.payment_method_installment_bay_title)).check(matches(isDisplayed()))
        onView(withListId(R.id.recycler_view).atPosition(0)).check(matches(hasDescendant(withText("3 months"))))
        onView(withListId(R.id.recycler_view).atPosition(1)).check(matches(hasDescendant(withText("4 months"))))
        onView(withListId(R.id.recycler_view).atPosition(2)).check(matches(hasDescendant(withText("6 months"))))
        onView(withListId(R.id.recycler_view).atPosition(3)).check(matches(hasDescendant(withText("9 months"))))
        onView(withListId(R.id.recycler_view).atPosition(4)).check(matches(hasDescendant(withText("10 months"))))
        onView(withId(R.id.recycler_view)).check(matches(itemCount(paymentMethod.installmentTerms!!.size)))
    }

    @Test
    fun displayAllowedInstallmentTerms_showOnlyAllowedTermsForRequestedAmountFromArgument() {
        mockRequester =
            mock {
                on { amount }.doReturn(200000L)
                on { currency }.doReturn("thb")
                on { capability }.doReturn(
                    Capability.create(
                        sourceTypes = emptyList(),
                        tokenizationMethods = emptyList(),
                        zeroInterestInstallments = true,
                    ),
                )
            }
        fragment =
            InstallmentTermChooserFragment.newInstance(paymentMethod).apply {
                requester = mockRequester
            }
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity {
            it.replaceFragment(fragment)
        }

        onView(withText(R.string.payment_method_installment_bay_title)).check(matches(isDisplayed()))

        onView(withListId(R.id.recycler_view).atPosition(0)).check(matches(hasDescendant(withText("3 months"))))
        onView(withListId(R.id.recycler_view).atPosition(1)).check(matches(hasDescendant(withText("4 months"))))
        onView(withId(R.id.recycler_view)).check(matches(itemCount(2)))
    }

    @Test
    fun clickInstallmentTerm_sendRequestToCreateSourceIfNoTokenRequired() {
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity {
            it.replaceFragment(fragment)
        }
        onView(withText(R.string.payment_method_installment_bay_title)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_view))
            .perform(actionOnItemAtPosition<OmiseItemViewHolder>(0, click()))

        onView(withId(R.id.recycler_view)).check(matches(not(isEnabled())))
        verify(mockRequester).request(any(), any())
    }

    @Test
    fun clickInstallmentTerm_opensCreditCardScreenWhenNeeded() {
        // required for wlb installments
        paymentMethod =
            PaymentMethod(
                name = "installment_wlb_ktc",
                installmentTerms = listOf(3, 4, 5, 6, 7, 8, 9, 10),
            )

        fragment =
            InstallmentTermChooserFragment.newInstance(paymentMethod).apply {
                requester = mockRequester
            }
        // Create an Intent with the required data to open credit card Activity
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), TestFragmentActivity::class.java).apply {
                putExtra(OmiseActivity.EXTRA_PKEY, "test_1234")
            }
        ActivityScenario.launch<TestFragmentActivity>(intent).onActivity {
            it.replaceFragment(fragment)
        }
        onView(withText(R.string.payment_method_installment_ktc_title)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_view))
            .perform(actionOnItemAtPosition<OmiseItemViewHolder>(0, click()))

        verify(mockRequester, never()).request(any(), any())
        // check that the credit card activity is displayed
        intended(hasComponent(CreditCardActivity::class.java.name))
        intended(hasExtraWithKey(OmiseActivity.EXTRA_SELECTED_INSTALLMENTS_TERM))
    }
}
