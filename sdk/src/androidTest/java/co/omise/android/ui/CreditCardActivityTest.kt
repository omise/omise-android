package co.omise.android.ui

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.models.ChargeStatus
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.Token
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.verifyNoMoreInteractions

@RunWith(AndroidJUnit4::class)
class CreditCardActivityTest {
    private lateinit var context: Context
    private lateinit var mockFlutterActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var mockActivityResultRegistry: ActivityResultRegistry

    private val publicKey = "pkey_test_123"

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        mockFlutterActivityLauncher = mock()
        mockActivityResultRegistry = mock()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(mockFlutterActivityLauncher)
        reset(mockFlutterActivityLauncher)
    }

    private fun createIntent(publicKey: String = this.publicKey): Intent {
        return Intent(context, CreditCardActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, publicKey)
        }
    }

    @Test
    fun onCreate_initializesCorrectly() {
        val intent = createIntent()
        val scenario = ActivityScenario.launch<CreditCardActivity>(intent)
        scenario.onActivity { activity ->
            val startedIntent = activity.intent
            assertEquals(publicKey, startedIntent?.getStringExtra(OmiseActivity.EXTRA_PKEY))
        }
    }

    @Test
    fun activityResult_processesTokenResultCorrectly() {
        val mockToken = Token(false, null, ChargeStatus.Pending, "object", "id")
        val mockSource = Source(SourceType.PromptPay)
        val resultIntent =
            Intent().apply {
                putExtra(OmiseActivity.EXTRA_TOKEN_OBJECT, mockToken)
                putExtra(OmiseActivity.EXTRA_SOURCE_OBJECT, mockSource)
            }
        ActivityScenario.launchActivityForResult<CreditCardActivity>(createIntent()).onActivity {
            it.handleFlutterResult(100, resultIntent)
        }
    }
}
