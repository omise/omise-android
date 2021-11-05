package co.omise.android.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.utils.itemCount
import co.omise.android.utils.withListId
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MobileBankingChooserFragmentTest {
    private lateinit var fragment: MobileBankingChooserFragment
    private val paymentMethods = listOf(
            PaymentMethod(name = "mobile_banking_kbank"),
            PaymentMethod(name = "mobile_banking_ocbc_pao"),
            PaymentMethod(name = "mobile_banking_scb")
    )

    private val mockRequest = mock<PaymentCreatorRequester<Source>> {
        on { amount }.doReturn(500000L)
        on { currency }.doReturn("thb")
    }

    @Before
    fun setUp() {
        fragment = MobileBankingChooserFragment.newInstance(paymentMethods).apply {
            requester = mockRequest
        }
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity {
            it.replaceFragment(fragment)
        }
        onView(withText(R.string.mobile_banking_chooser_title)).check(matches(isDisplayed()))
    }

    @Test
    fun displayAllowedBanks_showAllowedBanksFromArgument() {
        onView(withListId(R.id.recycler_view).atPosition(0)).check(matches(hasDescendant(withText(R.string.payment_method_mobile_banking_kbank_title))))
        onView(withListId(R.id.recycler_view).atPosition(1)).check(matches(hasDescendant(withText(R.string.payment_method_mobile_banking_ocbc_pao_title))))
        onView(withListId(R.id.recycler_view).atPosition(2)).check(matches(hasDescendant(withText(R.string.payment_method_mobile_banking_scb_title))))
        onView(withId(R.id.recycler_view)).check(matches(itemCount(paymentMethods.size)))
    }

    @Test
    fun clickBank_sendRequestToCreateSource() {
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition<OmiseItemViewHolder>(0, ViewActions.click()))
        onView(withId(R.id.recycler_view)).check(matches(not(isEnabled())))
        verify(mockRequest).request(any(), any())
    }
}
