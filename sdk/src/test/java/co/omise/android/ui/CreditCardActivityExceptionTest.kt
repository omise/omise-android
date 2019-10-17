package co.omise.android.ui

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreditCardActivityExceptionTest {

    private lateinit var scenario: ActivityScenario<CreditCardActivity>
    private val intent = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
        putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
    }

    @Before
    fun setUp() {
        scenario = launch(intent)
    }

    @Test(expected = IllegalAccessException::class)
    fun pkey_throwExceptionIfNotFound() {
        launch(CreditCardActivity::class.java)
    }
}
