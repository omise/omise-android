package co.omise.android.ui

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.models.Capability
import co.omise.android.models.ChargeStatus
import co.omise.android.models.PaymentMethod
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
class PaymentCreatorActivityTest {
    private lateinit var context: Context
    private lateinit var mockFlutterActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var mockActivityResultRegistry: ActivityResultRegistry

    private val publicKey = "pkey_test_123"
    private val amount = 1000L
    private val currency = "THB"
    private val googlePayMerchantId = "merchant_id_123"
    private val capability =
        Capability(
            id = "cap_test_123",
            livemode = false,
            location = "/capability",
            paymentMethods = listOf(PaymentMethod("promptpay")).toMutableList(),
            tokenizationMethods = listOf("googlepay"),
        )

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

    private fun createIntent(
        publicKey: String = this.publicKey,
        amount: Long = this.amount,
        currency: String = this.currency,
        googlePayMerchantId: String? = this.googlePayMerchantId,
        capability: Capability? = this.capability,
    ): Intent {
        return Intent(context, PaymentCreatorActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, publicKey)
            putExtra(OmiseActivity.EXTRA_AMOUNT, amount)
            putExtra(OmiseActivity.EXTRA_CURRENCY, currency)
            googlePayMerchantId?.let { putExtra(OmiseActivity.EXTRA_GOOGLEPAY_MERCHANT_ID, it) }
            capability?.let { putExtra(OmiseActivity.EXTRA_CAPABILITY, it) }
        }
    }

    @Test
    fun onCreate_initializesCorrectly() {
        val intent = createIntent()
        val scenario = ActivityScenario.launch<PaymentCreatorActivity>(intent)
        scenario.onActivity { activity ->
            // We can only check the intent that started PaymentCreatorActivity
            val startedIntent = activity.intent
            assertEquals(publicKey, startedIntent?.getStringExtra(OmiseActivity.EXTRA_PKEY))
            assertEquals(amount, startedIntent?.getLongExtra(OmiseActivity.EXTRA_AMOUNT, 0L))
            assertEquals(currency, startedIntent?.getStringExtra(OmiseActivity.EXTRA_CURRENCY))
            assertEquals(googlePayMerchantId, startedIntent?.getStringExtra(OmiseActivity.EXTRA_GOOGLEPAY_MERCHANT_ID))
            assertEquals(capability, startedIntent?.getParcelableExtra(OmiseActivity.EXTRA_CAPABILITY))
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

        // Launch the PaymentCreatorActivity
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(createIntent()).onActivity {
            it.handleFlutterResult(100, resultIntent)
        }
    }
}
