package co.omise.android.ui

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaymentCreatorActivityExceptionTest {
    private lateinit var scenario: ActivityScenario<PaymentCreatorActivity>

    @Test(expected = IllegalArgumentException::class)
    fun initialActivity_missExtrasIntent() {
        val noExtrasIntent = Intent(ApplicationProvider.getApplicationContext(), PaymentCreatorActivity::class.java)
        scenario = ActivityScenario.launch(noExtrasIntent)
    }
}
