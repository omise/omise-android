package co.omise.android.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
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
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class InternetBankingChooserFragmentTest {

    private lateinit var fragment: InternetBankingChooserFragment
    private val paymentMethods = listOf(
            PaymentMethod(name = "internet_banking_bbl"),
            PaymentMethod(name = "internet_banking_bay")
    )
    private val mockRequest = mock<PaymentCreatorRequester<Source>> {
        on { amount }.doReturn(500000L)
        on { currency }.doReturn("thb")
    }

    @Before
    fun setUp() {
        fragment = InternetBankingChooserFragment.newInstance(paymentMethods).apply { requester = mockRequest }
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity { it.replaceFragment(fragment) }
        onView(withText(R.string.internet_banking_chooser_title)).check(matches(isDisplayed()))
    }

    @Test
    fun displayAllowedBanks_showAllowedBanksFromArgument() {
        onView(withListId(R.id.recycler_view).atPosition(0)).check(matches(hasDescendant(withText(R.string.payment_method_internet_banking_bbl_title))))
        onView(withListId(R.id.recycler_view).atPosition(1)).check(matches(hasDescendant(withText(R.string.payment_method_internet_banking_bay_title))))
        onView(withId(R.id.recycler_view)).check(matches(itemCount(paymentMethods.size)))
    }

    @Test
    fun clickBank_sendRequestToCreateSource() {
        onView(withId(R.id.recycler_view))
                .perform(actionOnItemAtPosition<OmiseItemViewHolder>(0, click()))

        onView(withId(R.id.recycler_view)).check(matches(not(isEnabled())))
        verify(mockRequest).request(any(), any())
    }
}
