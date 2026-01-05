package co.omise.android.ui

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.omise.android.R
import co.omise.android.api.Client
import co.omise.android.api.Request
import co.omise.android.api.RequestListener
import co.omise.android.models.Bank
import co.omise.android.models.Capability
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.SupportedEcontext
import co.omise.android.models.Token
import co.omise.android.models.TokenizationMethod
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_TOKEN
import co.omise.android.utils.itemCount
import co.omise.android.utils.withListId
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class PaymentCreatorActivityTest {
    @get:Rule
    val intentRule = IntentsRule()

    // capabilities requested by the merchant
    private val capability =
        Capability.create(
            allowCreditCard = true,
            sourceTypes = listOf(SourceType.Fpx(), SourceType.TrueMoney),
            tokenizationMethods = listOf(TokenizationMethod.GooglePay),
        )
    private val mockClient: Client = mock()
    private val intent =
        Intent(
            ApplicationProvider.getApplicationContext(),
            PaymentCreatorActivity::class.java,
        ).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
            putExtra(OmiseActivity.EXTRA_AMOUNT, 50000)
            putExtra(OmiseActivity.EXTRA_CURRENCY, "thb")
            putExtra(OmiseActivity.EXTRA_CAPABILITY, capability)
        }
    private val application = (InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application)
    private val activityLifecycleCallbacks =
        object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(
                activity: Activity,
                savedInstanceState: Bundle?,
            ) {
                (activity as? PaymentCreatorActivity)?.setClient(mockClient)
            }

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(
                activity: Activity,
                outState: Bundle,
            ) {}

            override fun onActivityDestroyed(activity: Activity) {}
        }

    @Before
    fun setUp() {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        whenever(mockClient.send(any<Request<Capability>>(), any())).doAnswer { invocation ->
            val callback = invocation.getArgument<RequestListener<Capability>>(1)
            // Capabilities retrieved from api
            callback.onRequestSucceed(
                Capability.create(
                    allowCreditCard = true,
                    sourceTypes = listOf(SourceType.TrueMoney, SourceType.PromptPay),
                    tokenizationMethods = listOf(TokenizationMethod.GooglePay, TokenizationMethod.Card),
                ),
            )
        }
    }

    @After
    fun tearDown() {
        reset(mockClient)
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    @Test
    fun initialActivity_collectExtrasIntent() {
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent)

        onView(withId(R.id.payment_creator_container)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToCreditCardForm_startCreditCartActivity() {
        var activity: PaymentCreatorActivity? = null
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        activity?.navigation?.navigateToCreditCardForm()

        intended(hasComponent(hasClassName(CreditCardActivity::class.java.name)))
    }

    @Test
    fun shouldShowOnlyPaymentMethodsRequestedByMerchantAndAvailableInCapability() {
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent)

        onView(
            withListId(R.id.recycler_view).atPosition(0),
        ).check(matches(ViewMatchers.isEnabled()))
            .check(matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.payment_method_credit_card_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(1),
        ).check(matches(ViewMatchers.isEnabled()))
            .check(matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.payment_truemoney_title))))
        onView(
            withListId(R.id.recycler_view).atPosition(2),
        ).check(matches(ViewMatchers.isEnabled()))
            .check(matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.googlepay))))
        onView(ViewMatchers.withText(R.string.payment_method_fpx_title)).check(doesNotExist())
        onView(withId(R.id.recycler_view))
            .check(matches(itemCount(3)))
    }

    @Test
    fun creditCardResult_resultOk() {
        val creditCardIntent =
            Intent().apply {
                putExtra(EXTRA_TOKEN, Token())
            }
        val scenario =
            ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
                it.performActivityResult(100, RESULT_OK, creditCardIntent)
            }

        assertEquals(RESULT_OK, scenario.result.resultCode)
    }

    @Test
    fun flagSecure_whenParameterIsFalseThenAttributesMustNotContainFlagSecure() {
        intent.putExtra(OmiseActivity.EXTRA_IS_SECURE, false)
        val scenario = ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent)
        scenario.onActivity {
            assertNotEquals(WindowManager.LayoutParams.FLAG_SECURE, it.window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    @Test
    fun flagSecure_whenParameterNotSetThenAttributesMustContainFlagSecure() {
        val scenario = ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent)
        scenario.onActivity {
            assertEquals(WindowManager.LayoutParams.FLAG_SECURE, it.window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    @Test
    fun navigateToInternetBankingChooser_addsFragmentToBackStack() {
        var activity: PaymentCreatorActivity? = null
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        // Wait for capability to load and navigation to be initialized
        Thread.sleep(500)

        val allowedBanks = listOf(PaymentMethod("internet_banking_test"))
        activity?.navigation?.navigateToInternetBankingChooser(allowedBanks)

        Thread.sleep(100)
        val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.payment_creator_container)
        assert(fragment is InternetBankingChooserFragment)
    }

    @Test
    fun navigateToMobileBankingChooser_addsFragmentToBackStack() {
        var activity: PaymentCreatorActivity? = null
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        Thread.sleep(500)

        val allowedBanks = listOf(PaymentMethod("mobile_banking_test"))
        activity?.navigation?.navigateToMobileBankingChooser(allowedBanks)

        Thread.sleep(100)
        val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.payment_creator_container)
        assert(fragment is MobileBankingChooserFragment)
    }

    @Test
    fun navigateToInstallmentChooser_addsFragmentToBackStack() {
        var activity: PaymentCreatorActivity? = null
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        Thread.sleep(500)

        val allowedInstalls = listOf(PaymentMethod("installment_test"))
        activity?.navigation?.navigateToInstallmentChooser(allowedInstalls)

        Thread.sleep(100)
        val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.payment_creator_container)
        assert(fragment is InstallmentChooserFragment)
    }

    @Test
    fun navigateToInstallmentTermChooser_addsFragmentToBackStack() {
        var activity: PaymentCreatorActivity? = null
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        Thread.sleep(500)

        val installment = PaymentMethod("installment_bay")
        activity?.navigation?.navigateToInstallmentTermChooser(installment)

        Thread.sleep(100)
        val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.payment_creator_container)
        assert(fragment is InstallmentTermChooserFragment)
    }

    @Test
    fun navigateToEContextForm_addsFragmentToBackStack() {
        var activity: PaymentCreatorActivity? = null
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        Thread.sleep(500)

        val eContext = SupportedEcontext.ConvenienceStore
        activity?.navigation?.navigateToEContextForm(eContext)

        Thread.sleep(100)
        val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.payment_creator_container)
        assert(fragment is EContextFormFragment)
    }

    @Test
    fun navigateToAtomeForm_addsFragmentToBackStack() {
        var activity: PaymentCreatorActivity? = null
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        Thread.sleep(500)

        activity?.navigation?.navigateToAtomeForm()

        Thread.sleep(100)
        val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.payment_creator_container)
        assert(fragment is AtomeFormFragment)
    }

    @Test
    fun createSourceFinished_returnsResultWithSource() {
        val source = Source()
        var activity: PaymentCreatorActivity? = null
        val scenario = ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent)

        scenario.onActivity {
            activity = it
        }

        Thread.sleep(500)

        activity?.navigation?.createSourceFinished(source)

        Thread.sleep(100)

        assertEquals(RESULT_OK, scenario.result.resultCode)
        assert(scenario.result.resultData.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT))
    }

    @Test
    fun navigateToTrueMoneyForm_addsFragmentToBackStack() {
        var activity: PaymentCreatorActivity? = null
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        Thread.sleep(500)

        activity?.navigation?.navigateToTrueMoneyForm()

        Thread.sleep(100)
        val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.payment_creator_container)
        assert(fragment is TrueMoneyFormFragment)
    }

    @Test
    fun navigateToFpxEmailForm_addsFragmentToBackStack() {
        var activity: PaymentCreatorActivity? = null
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        Thread.sleep(500)

        activity?.navigation?.navigateToFpxEmailForm()

        Thread.sleep(100)
        val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.payment_creator_container)
        assert(fragment is FpxEmailFormFragment)
    }

    @Test
    fun navigateToFpxBankChooser_addsFragmentToBackStack() {
        var activity: PaymentCreatorActivity? = null
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        Thread.sleep(500)

        val banks: List<Bank> = listOf(Bank())
        val email = "test@example.com"
        activity?.navigation?.navigateToFpxBankChooser(banks, email)

        Thread.sleep(100)
        val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.payment_creator_container)
        assert(fragment is FpxBankChooserFragment)
    }

    @Test
    fun navigateToGooglePayForm_startsGooglePayActivity() {
        var activity: PaymentCreatorActivity? = null
        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        Thread.sleep(500)

        activity?.navigation?.navigateToGooglePayForm()

        intended(hasComponent(hasClassName(GooglePayActivity::class.java.name)))
    }

    @Test
    fun navigateToDuitNowOBWBankChooser_addsFragmentToBackStack() {
        var activity: PaymentCreatorActivity? = null
        val capabilityWithDuitNow =
            Capability.create(
                allowCreditCard = true,
                sourceTypes = listOf(SourceType.DuitNowOBW),
            )

        whenever(mockClient.send(any<Request<Capability>>(), any())).doAnswer { invocation ->
            val callback = invocation.getArgument<RequestListener<Capability>>(1)
            callback.onRequestSucceed(capabilityWithDuitNow)
        }

        ActivityScenario.launchActivityForResult<PaymentCreatorActivity>(intent).onActivity {
            activity = it
        }

        Thread.sleep(500)

        activity?.navigation?.navigateToDuitNowOBWBankChooser(capabilityWithDuitNow)

        Thread.sleep(100)
        val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.payment_creator_container)
        assert(fragment is DuitNowOBWBankChooserFragment)
    }
}
