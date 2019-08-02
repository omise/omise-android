package co.omise.android.ui

import android.os.Bundle
import androidx.fragment.app.testing.launchFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.CardBrand
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurityCodeTooltipDialogFragmentTest {

    @Test
    fun content_3Digits() {
        val argument = Bundle()
        argument.putParcelable(SecurityCodeTooltipDialogFragment.EXTRA_CARD_BRAND, CardBrand.MASTERCARD)

        with(launchFragment<SecurityCodeTooltipDialogFragment>(argument, R.style.OmiseTheme)) {
            onFragment { fragment ->
                assertNotNull(fragment.dialog)
                assertTrue(fragment.dialog!!.isShowing)
            }
        }

        onView(withId(R.id.cvv_description_text)).check(matches(withText("3 digit number on the back of your card")))
    }


    @Test
    fun content_4Digits() {
        val argument = Bundle()
        argument.putParcelable(SecurityCodeTooltipDialogFragment.EXTRA_CARD_BRAND, CardBrand.AMEX)

        with(launchFragment<SecurityCodeTooltipDialogFragment>(argument, R.style.OmiseTheme)) {
            onFragment { fragment ->
                assertNotNull(fragment.dialog)
                assertTrue(fragment.dialog!!.isShowing)
            }
        }

        onView(withId(R.id.cvv_description_text)).check(matches(withText("4 digit number on the front of your card")))
    }
}
