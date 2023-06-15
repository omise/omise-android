package co.omise.android.ui

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView.*
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.omise.android.R
import co.omise.android.models.CountryInfo
import co.omise.android.utils.itemCount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountryListDialogFragmentTest {
    private lateinit var scenario: ActivityScenario<TestFragmentActivity>
    private val intent = Intent(InstrumentationRegistry.getInstrumentation().context, TestFragmentActivity::class.java)

    @Before
    fun setUp() {
        scenario = ActivityScenario.launchActivityForResult(intent)
    }

    @Test
    fun countryList_showAllCountries() {
        val dialog = CountryListDialogFragment()
        scenario.onActivity {
            dialog.show(it.supportFragmentManager, null)
        }

        onView(withId(R.id.country_list))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withId(R.id.country_list)).check(matches(itemCount(CountryInfo.ALL.size)))
        assertTrue(dialog.isVisible)
    }

    @Test
    fun countryList_returnSelectedCountry() {
        var selectedCountry: CountryInfo? = null
        val expectedCountry = CountryInfo.ALL.find { it.code == "US" }
        val dialog = CountryListDialogFragment()
        dialog.listener = object : CountryListDialogFragment.CountryListDialogListener {
            override fun onCountrySelected(country: CountryInfo) {
                selectedCountry = country
            }
        }
        scenario.onActivity {
            dialog.show(it.supportFragmentManager, null)
        }

        onView(withId(R.id.country_list))
            .inRoot(isDialog())
            .perform(
                actionOnItem<ViewHolder>(
                    hasDescendant(withText(expectedCountry!!.name)),
                    click()
                )
            )

        assertEquals(expectedCountry, selectedCountry)
        assertFalse(dialog.isVisible)
    }

    @Test
    fun closeButton_shouldCloseDialog() {
        val dialog = CountryListDialogFragment()
        scenario.onActivity {
            dialog.show(it.supportFragmentManager, null)
        }

        onView(withId(R.id.close_button))
            .inRoot(isDialog())
            .perform(click())

        assertFalse(dialog.isVisible)
    }
}
