package co.omise.android.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.Bank
import co.omise.android.models.Source
import co.omise.android.utils.itemCount
import co.omise.android.utils.withListId
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class FpxBankChooserFragmentTest {
    private val banks =
        listOf(
            Bank("Affin Bank", "affin", true),
            Bank("AGRONet", "agro", false),
            Bank("Alliance Bank (Personal)", "alliance", true),
        )

    private val mockRequester: PaymentCreatorRequester<Source> =
        mock {
            on { amount }.doReturn(40000L)
            on { currency }.doReturn("myr")
        }

    private val fragment =
        FpxBankChooserFragment.newInstance(banks, "example@omise.co").apply {
            requester = mockRequester
        }

    @Before
    fun setUp() {
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity {
            it.replaceFragment(fragment)
        }

        onView(withText(R.string.payment_method_fpx_title)).check(matches(isDisplayed()))
    }

    @Test
    fun displayAllowedBanks_showAllowedBanksFromArgument() {
        onView(withListId(R.id.recycler_view).atPosition(0)).check(matches(hasDescendant(withText("Affin Bank"))))
        onView(withListId(R.id.recycler_view).atPosition(1)).check(matches(hasDescendant(withText("AGRONet"))))
        onView(withListId(R.id.recycler_view).atPosition(2)).check(matches(hasDescendant(withText("Alliance Bank (Personal)"))))

        onView(withId(R.id.recycler_view)).check(matches(itemCount(banks.size)))
    }

    @Test
    fun greyOutBanks_showGreyOutedBanksFromCapability() {
        onView(withListId(R.id.recycler_view).atPosition(0)).check(matches(isEnabled()))
        onView(withListId(R.id.recycler_view).atPosition(1)).check(matches(not(isEnabled())))
        onView(withListId(R.id.recycler_view).atPosition(2)).check(matches(isEnabled()))
    }

    @Test
    fun clickInstallmentTerm_sendRequestToCreateSource() {
        onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<OmiseItemViewHolder>(0, click()))

        onView(withId(R.id.recycler_view)).check(matches(not(isEnabled())))
        verify(mockRequester).request(any(), any())
    }
}
