package co.omise.android.ui

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.testing.launchFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.CardBrand
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurityCodeTooltipDialogFragmentInstrumentedTest {

    @Test
    fun closeButton_dismissDialog() {
        val argument = Bundle()
        argument.putParcelable(SecurityCodeTooltipDialogFragment.EXTRA_CARD_BRAND, CardBrand.MASTERCARD)
        var dialog: DialogFragment? = null
        with(launchFragment<SecurityCodeTooltipDialogFragment>(argument, R.style.OmiseTheme)) {
            onFragment { fragment ->
                dialog = fragment
                assertNotNull(dialog!!.dialog)
                assertTrue(dialog!!.dialog!!.isShowing)
            }
        }

        onView(withId(R.id.close_button)).inRoot(isDialog()).perform(click())

        assertFalse(dialog?.dialog?.isShowing ?: false)
    }
}