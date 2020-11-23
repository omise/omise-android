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
import co.omise.android.models.Capability
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
class InstallmentTermChooserFragmentTest {

    private val paymentMethod = PaymentMethod(
            name = "installment_bay",
            installmentTerms = listOf(3, 4, 6, 9, 10))
    private val mockRequester: PaymentCreatorRequester<Source> = mock {
        on { amount }.doReturn(500000L)
        on { currency }.doReturn("thb")
        on { capability }.doReturn(Capability.create(sourceTypes = emptyList()))
    }

    private val fragment = InstallmentTermChooserFragment.newInstance(paymentMethod).apply {
        requester = mockRequester
    }

    @Before
    fun setUp() {
        ActivityScenario.launch(TestFragmentActivity::class.java).onActivity {
            it.replaceFragment(fragment)
        }

        onView(withText(R.string.payment_method_installment_bay_title)).check(matches(isDisplayed()))
    }

    @Test
    fun displayAllowedInstallmentTerms_showAllowedInstallmentTermsFromArgument() {
        onView(withListId(R.id.recycler_view).atPosition(0)).check(matches(hasDescendant(withText("3 months"))))
        onView(withListId(R.id.recycler_view).atPosition(1)).check(matches(hasDescendant(withText("4 months"))))
        onView(withListId(R.id.recycler_view).atPosition(2)).check(matches(hasDescendant(withText("6 months"))))
        onView(withListId(R.id.recycler_view).atPosition(3)).check(matches(hasDescendant(withText("9 months"))))
        onView(withListId(R.id.recycler_view).atPosition(4)).check(matches(hasDescendant(withText("10 months"))))
        onView(withId(R.id.recycler_view)).check(matches(itemCount(paymentMethod.installmentTerms!!.size)))
    }

    @Test
    fun clickInstallmentTerm_sendRequestToCreateSource() {
        onView(withId(R.id.recycler_view))
                .perform(actionOnItemAtPosition<OmiseItemViewHolder>(0, click()))

        onView(withId(R.id.recycler_view)).check(matches(not(isEnabled())))
        verify(mockRequester).request(any(), any())
    }
}