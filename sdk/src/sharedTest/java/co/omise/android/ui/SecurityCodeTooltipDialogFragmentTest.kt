package co.omise.android.ui

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.testing.launchFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.R
import co.omise.android.models.CardBrand
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurityCodeTooltipDialogFragmentTest {

    @Test
    fun createDialog_brandIsNull() {
        val argument = Bundle()
        with(launchFragment<SecurityCodeTooltipDialogFragment>(argument, R.style.OmiseTheme)) {
            onFragment { fragment ->
                assertNotNull(fragment.dialog)
                assertTrue(fragment.dialog!!.isShowing)
            }
        }

        onView(withId(R.id.cvv_image))
                .inRoot(isDialog())
                .check(matches(withImageResource(R.drawable.cvv_3_digits)))
        onView(withId(R.id.cvv_description_text))
                .inRoot(isDialog())
                .check(matches(withText(R.string.cvv_tooltip_3_digits)))
    }

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

        onView(withId(R.id.cvv_image))
                .inRoot(isDialog())
                .check(matches(withImageResource(R.drawable.cvv_3_digits)))
        onView(withId(R.id.cvv_description_text))
                .inRoot(isDialog())
                .check(matches(withText(R.string.cvv_tooltip_3_digits)))
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

        onView(withId(R.id.cvv_image))
                .inRoot(isDialog())
                .check(matches(withImageResource(R.drawable.cvv_4_digits)))
        onView(withId(R.id.cvv_description_text))
                .inRoot(isDialog())
                .check(matches(withText(R.string.cvv_tooltip_4_digits)))
    }

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

        assertNull(dialog!!.dialog)
    }

    private fun withImageResource(@DrawableRes imageRes: Int): Matcher<View> =
            object : TypeSafeMatcher<View>() {
                override fun describeTo(description: Description?) {
                    description
                            ?.appendText("with image from resource id:")
                            ?.appendValue(imageRes)
                }

                override fun matchesSafely(item: View?): Boolean {
                    val imageView = item as? ImageView ?: return false
                    val actualBitmap = (imageView.drawable as BitmapDrawable).bitmap
                    val expectedBitmap = BitmapFactory.decodeResource(imageView.resources, imageRes)
                    return actualBitmap.sameAs(expectedBitmap)
                }

            }
}
