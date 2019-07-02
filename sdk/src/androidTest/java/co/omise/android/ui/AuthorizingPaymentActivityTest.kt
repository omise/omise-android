package co.omise.android.ui

import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthorizingPaymentActivityTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(AuthorizingPaymentActivity::class.java)

    @Before
    fun setUp() {
    }

    @Test
    fun verifyURL_verified() {

    }
}
