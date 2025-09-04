package co.omise.android.ui

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launchActivityForResult
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.hasFocus
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.omise.android.R
import co.omise.android.api.Client
import co.omise.android.api.Request
import co.omise.android.api.RequestListener
import co.omise.android.models.Capability
import co.omise.android.models.CardHolderDataField
import co.omise.android.models.CardHolderDataList
import co.omise.android.models.CardParam
import co.omise.android.models.Token
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class CreditCardActivityTest {
    private lateinit var scenario: ActivityScenario<CreditCardActivity>
    private val intent =
        Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
        }
    private val application = (InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application)
    private val activityLifecycleCallbacks =
        object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(
                activity: Activity,
                savedInstanceState: Bundle?,
            ) {
                (activity as? CreditCardActivity)?.setClient(mockClient)
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
    private val mockClient: Client = mock()

    @Before
    fun setUp() {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        whenever(mockClient.send(any<Request<Capability>>(), any())).doAnswer { invocation ->
            val callback = invocation.getArgument<RequestListener<Capability>>(1)
            callback.onRequestSucceed(Capability(country = "TH"))
        }

        scenario = launchActivityForResult(intent)
    }

    @After
    fun tearDown() {
        reset(mockClient)
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    @Test
    fun form_validForm() {
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeNumberText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"), pressImeActionButton())

        onView(withId(R.id.edit_card_number)).check(matches(withText("4242 4242 4242 4242")))
        onView(withId(R.id.edit_card_name)).check(matches(withText("John Doe")))
        onView(withId(R.id.edit_expiry_date)).check(matches(withText("12/34")))
        onView(withId(R.id.edit_security_code)).check(matches(withText("123")))
        onView(withId(R.id.edit_country)).check(matches(withText("Thailand")))
        onView(withId(R.id.billing_address_container)).check(matches(not(isDisplayed())))
        onView(withId(R.id.button_submit)).check(matches(isEnabled()))
    }

    @Test
    fun form_validBillingAddressForm() {
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeNumberText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"))
        onView(withId(R.id.edit_country)).perform(scrollTo(), click())

        onView(withId(R.id.country_list))
            .inRoot(isDialog())
            .perform(
                actionOnItem<ViewHolder>(hasDescendant(withText("United States of America")), click()),
            )
        onView(withId(R.id.edit_street1)).perform(scrollTo(), typeText("311 Sanders Hill Rd"))
        onView(withId(R.id.edit_city)).perform(scrollTo(), typeText("Strykersville"))
        onView(withId(R.id.edit_state)).perform(scrollTo(), typeText("New York"))
        onView(withId(R.id.edit_postal_code)).perform(scrollTo(), typeNumberText("14145"), pressImeActionButton())

        onView(withId(R.id.edit_card_number)).check(matches(withText("4242 4242 4242 4242")))
        onView(withId(R.id.edit_card_name)).check(matches(withText("John Doe")))
        onView(withId(R.id.edit_expiry_date)).check(matches(withText("12/34")))
        onView(withId(R.id.edit_security_code)).check(matches(withText("123")))
        onView(withId(R.id.edit_country)).check(matches(withText("United States of America")))
        onView(withId(R.id.edit_street1)).check(matches(withText("311 Sanders Hill Rd")))
        onView(withId(R.id.edit_city)).check(matches(withText("Strykersville")))
        onView(withId(R.id.edit_state)).check(matches(withText("New York")))
        onView(withId(R.id.edit_postal_code)).check(matches(withText("14145")))
        onView(withId(R.id.button_submit)).check(matches(isEnabled()))
    }

    @Test
    fun form_invalidForm() {
        onView(withId(R.id.edit_card_number)).perform(typeText("1234567890"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeNumberText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"), pressImeActionButton())

        onView(withId(R.id.edit_card_number)).check(matches(withText("1234 5678 90")))
        onView(withId(R.id.edit_card_name)).check(matches(withText("John Doe")))
        onView(withId(R.id.edit_expiry_date)).check(matches(withText("12/34")))
        onView(withId(R.id.edit_security_code)).check(matches(withText("123")))
        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun form_invalidFormIfBillingAddressFieldsAreNotProvided() {
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"))
        onView(withId(R.id.edit_country)).perform(scrollTo(), click())

        onView(withId(R.id.country_list))
            .inRoot(isDialog())
            .perform(
                actionOnItem<ViewHolder>(hasDescendant(withText("Canada")), click()),
            )

        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun form_invalidFormIfPostalCodeIsNotProvided() {
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"))
        onView(withId(R.id.edit_country)).perform(scrollTo(), click())

        onView(withId(R.id.country_list))
            .inRoot(isDialog())
            .perform(
                actionOnItem<ViewHolder>(hasDescendant(withText("Canada")), click()),
            )
        onView(withId(R.id.edit_street1)).perform(scrollTo(), typeText("125 Harbour Dr"))
        onView(withId(R.id.edit_city)).perform(scrollTo(), typeText("St. John's"))
        onView(withId(R.id.edit_state)).perform(scrollTo(), typeText("Newfoundland and Labrador"))

        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun form_invalidFormIfStateIsNotProvided() {
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"))
        onView(withId(R.id.edit_country)).perform(scrollTo(), click())

        onView(withId(R.id.country_list))
            .inRoot(isDialog())
            .perform(
                actionOnItem<ViewHolder>(hasDescendant(withText("Canada")), click()),
            )
        onView(withId(R.id.edit_street1)).perform(scrollTo(), typeText("125 Harbour Dr"))
        onView(withId(R.id.edit_city)).perform(scrollTo(), typeText("St. John's"))
        onView(withId(R.id.edit_postal_code)).perform(scrollTo(), typeText("A1C 6N6"))

        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun form_invalidFormIfStreet1AndCityAreNotProvided() {
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"))
        onView(withId(R.id.edit_country)).perform(scrollTo(), click())

        onView(withId(R.id.country_list))
            .inRoot(isDialog())
            .perform(
                actionOnItem<ViewHolder>(hasDescendant(withText("Canada")), click()),
            )
        onView(withId(R.id.edit_state)).perform(scrollTo(), typeText("Newfoundland and Labrador"))
        onView(withId(R.id.edit_postal_code)).perform(scrollTo(), typeText("A1C 6N6"))

        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun postTalCode_canTypeAlphabet() {
        onView(withId(R.id.edit_country)).perform(scrollTo(), click())
        onView(withId(R.id.country_list))
            .inRoot(isDialog())
            .perform(
                actionOnItem<ViewHolder>(hasDescendant(withText("United States of America")), click()),
            )

        onView(withId(R.id.edit_postal_code)).perform(scrollTo(), typeText("BN1 1EE"))

        onView(withId(R.id.edit_postal_code)).check(matches(withText("BN1 1EE")))
    }

    @Test
    fun postTalCode_canTypeStartWithZero() {
        onView(withId(R.id.edit_country)).perform(scrollTo(), click())
        onView(withId(R.id.country_list))
            .inRoot(isDialog())
            .perform(
                actionOnItem<ViewHolder>(hasDescendant(withText("United States of America")), click()),
            )

        onView(withId(R.id.edit_postal_code)).perform(scrollTo(), typeText("000022"))

        onView(withId(R.id.edit_postal_code)).check(matches(withText("000022")))
    }

    @Test
    fun state_canTypeWhitespacesApostrophesPeriod() {
        onView(withId(R.id.edit_country)).perform(scrollTo(), click())
        onView(withId(R.id.country_list))
            .inRoot(isDialog())
            .perform(
                actionOnItem<ViewHolder>(hasDescendant(withText("United States of America")), click()),
            )

        onView(withId(R.id.edit_state)).perform(scrollTo(), typeText("St. John's,"))

        onView(withId(R.id.edit_state)).check(matches(withText("St. John's,")))
    }

    @Test
    fun forwardFocus_rightDirection() {
        onView(withId(R.id.edit_card_number)).perform(click(), pressImeActionButton())

        onView(withId(R.id.edit_card_name)).check(matches(hasFocus()))

        onView(withId(R.id.edit_card_name)).perform(pressImeActionButton())

        onView(withId(R.id.edit_expiry_date)).check(matches(hasFocus()))

        onView(withId(R.id.edit_expiry_date)).perform(pressImeActionButton())

        onView(withId(R.id.edit_security_code)).check(matches(hasFocus()))
    }

    @Test
    fun submitForm_disableFormWhenPressSubmit() {
        whenever(mockClient.send<Token>(any(), any())).doAnswer {}
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeNumberText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"), closeSoftKeyboard())
        onView(withId(R.id.button_submit)).perform(scrollTo(), click())

        onView(withId(R.id.edit_card_number)).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_card_name)).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_expiry_date)).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_security_code)).check(matches(not(isEnabled())))
        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun submitForm_verifyRequestBody() {
        val tokenRequestCaptor = argumentCaptor<Request<Token>>()
        whenever(mockClient.send(tokenRequestCaptor.capture(), any())).doAnswer { invocation ->
            val callback = invocation.getArgument<RequestListener<Token>>(1)
            callback.onRequestSucceed(Token())
        }

        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeNumberText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"), closeSoftKeyboard())
        onView(withId(R.id.button_submit)).perform(scrollTo(), click())

        assertEquals(
            CardParam(
                number = "4242424242424242",
                name = "John Doe",
                expirationMonth = 12,
                expirationYear = 2034,
                securityCode = "123",
                country = "TH",
            ),
            (tokenRequestCaptor.firstValue.builder as Token.CreateTokenRequestBuilder).card,
        )
    }

    @Test
    fun submitForm_verifyRequestBodyWithBillingAddress() {
        val tokenRequestCaptor = argumentCaptor<Request<Token>>()
        whenever(mockClient.send(tokenRequestCaptor.capture(), any())).doAnswer { invocation ->
            val callback = invocation.getArgument<RequestListener<Token>>(1)
            callback.onRequestSucceed(Token())
        }

        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeNumberText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"), closeSoftKeyboard())
        onView(withId(R.id.edit_country)).perform(scrollTo(), click())
        onView(withId(R.id.country_list))
            .inRoot(isDialog())
            .perform(
                actionOnItem<ViewHolder>(hasDescendant(withText("United States of America")), click()),
            )
        onView(withId(R.id.edit_street1)).perform(scrollTo(), typeText("311 Sanders Hill Rd"))
        onView(withId(R.id.edit_city)).perform(scrollTo(), typeText("Strykersville"))
        onView(withId(R.id.edit_state)).perform(scrollTo(), typeText("New York"))
        onView(withId(R.id.edit_postal_code)).perform(scrollTo(), typeNumberText("14145"), pressImeActionButton())
        onView(withId(R.id.button_submit)).perform(scrollTo(), click())

        assertEquals(
            CardParam(
                number = "4242424242424242",
                name = "John Doe",
                expirationMonth = 12,
                expirationYear = 2034,
                securityCode = "123",
                country = "US",
                state = "New York",
                city = "Strykersville",
                street1 = "311 Sanders Hill Rd",
                postalCode = "14145",
            ),
            (tokenRequestCaptor.firstValue.builder as Token.CreateTokenRequestBuilder).card,
        )
    }

    @Test
    fun backPressed_setResultCanceled() {
        pressBackUnconditionally()
        val result = scenario.result
        assertEquals(RESULT_CANCELED, result.resultCode)
    }

    @Test
    fun flagSecure_whenParameterIsFalseThenAttributesMustNotContainFlagSecure() {
        intent.putExtra(OmiseActivity.EXTRA_IS_SECURE, false)
        val scenario = launchActivityForResult<CreditCardActivity>(intent)
        scenario.onActivity {
            assertNotEquals(WindowManager.LayoutParams.FLAG_SECURE, it.window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    @Test
    fun flagSecure_whenParameterNotSetThenAttributesMustContainFlagSecure() {
        val scenario = launchActivityForResult<CreditCardActivity>(intent)
        scenario.onActivity {
            assertEquals(WindowManager.LayoutParams.FLAG_SECURE, it.window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
    @Test
    fun cardHolderData_fieldsNotVisibleWhenNotRequested() {
        // Default scenario - no card holder data fields requested
        onView(withId(R.id.edit_email)).check(matches(not(isDisplayed())))
        onView(withId(R.id.text_email_title)).check(matches(not(isDisplayed())))
        onView(withId(R.id.edit_phone_number)).check(matches(not(isDisplayed())))
        onView(withId(R.id.text_phone_number_title)).check(matches(not(isDisplayed())))
    }
    @Test
    fun cardHolderData_onlyEmailFieldVisibleWhenEmailRequested() {
        // Create intent with only email field requested
        val intentWithEmail = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
            putExtra(OmiseActivity.EXTRA_CARD_HOLDER_DATA, CardHolderDataList(arrayListOf(
                CardHolderDataField.EMAIL)))
        }

        scenario = launchActivityForResult(intentWithEmail)

        onView(withId(R.id.edit_email)).check(matches(isDisplayed()))
        onView(withId(R.id.text_email_title)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_phone_number)).check(matches(not(isDisplayed())))
        onView(withId(R.id.text_phone_number_title)).check(matches(not(isDisplayed())))
    }
    fun cardHolderData_onlyPhoneFieldVisibleWhenPhoneRequested() {
        // Create intent with only phone field requested
        val intentWithPhone = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
            putExtra(OmiseActivity.EXTRA_CARD_HOLDER_DATA, CardHolderDataList(arrayListOf(CardHolderDataField.PHONE_NUMBER)))
        }

        scenario = launchActivityForResult(intentWithPhone)

        onView(withId(R.id.edit_email)).check(matches(not(isDisplayed())))
        onView(withId(R.id.text_email_title)).check(matches(not(isDisplayed())))
        onView(withId(R.id.edit_phone_number)).check(matches(isDisplayed()))
        onView(withId(R.id.text_phone_number_title)).check(matches(isDisplayed()))
    }
    fun cardHolderData_bothFieldsVisibleWhenBothRequested() {
        // Create intent with both fields requested
        val intentWithBothFields = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
            putExtra(OmiseActivity.EXTRA_CARD_HOLDER_DATA, CardHolderDataList(arrayListOf(CardHolderDataField.EMAIL, CardHolderDataField.PHONE_NUMBER)))
        }

        scenario = launchActivityForResult(intentWithBothFields)

        onView(withId(R.id.edit_email)).check(matches(isDisplayed()))
        onView(withId(R.id.text_email_title)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_phone_number)).check(matches(isDisplayed()))
        onView(withId(R.id.text_phone_number_title)).check(matches(isDisplayed()))
    }
    fun cardHolderData_invalidEmailTriggersErrorAndDisablesButton() {
        // Create intent with email field requested
        val intentWithEmail = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
            putExtra(OmiseActivity.EXTRA_CARD_HOLDER_DATA, CardHolderDataList(arrayListOf(CardHolderDataField.EMAIL)))
        }

        scenario = launchActivityForResult(intentWithEmail)

        // Fill in valid card information
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeNumberText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"))

        // Enter invalid email and lose focus
        onView(withId(R.id.edit_email)).perform(typeText("invalid-email"), pressImeActionButton())

        // Check that error is displayed and button is disabled
        onView(withId(R.id.text_email_error)).check(matches(isDisplayed()))
        onView(withId(R.id.text_email_error)).check(matches(withText(R.string.error_invalid_email)))
        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }
    fun cardHolderData_invalidPhoneTriggersErrorAndDisablesButton() {
        // Create intent with phone field requested
        val intentWithPhone = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
            putExtra(OmiseActivity.EXTRA_CARD_HOLDER_DATA, CardHolderDataList(arrayListOf(CardHolderDataField.PHONE_NUMBER)))
        }

        scenario = launchActivityForResult(intentWithPhone)

        // Fill in valid card information
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeNumberText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"))

        // Enter invalid phone and lose focus
        onView(withId(R.id.edit_phone_number)).perform(typeText("invalid-phone"), pressImeActionButton())

        // Check that error is displayed and button is disabled
        onView(withId(R.id.text_phone_number_error)).check(matches(isDisplayed()))
        onView(withId(R.id.text_phone_number_error)).check(matches(withText(R.string.error_invalid_phone_number)))
        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }
    @Test
    fun cardHolderData_validEmailAndPhoneEnablesButton() {
        // Create intent with both fields requested
        val intentWithBothFields = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
            putExtra(OmiseActivity.EXTRA_CARD_HOLDER_DATA, CardHolderDataList(arrayListOf(CardHolderDataField.EMAIL, CardHolderDataField.PHONE_NUMBER)))
        }

        scenario = launchActivityForResult(intentWithBothFields)

        // Fill in all valid information including card holder data
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeNumberText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"))
        onView(withId(R.id.edit_email)).perform(typeText("john.doe@example.com"))
        onView(withId(R.id.edit_phone_number)).perform(typeText("+1234567890"), pressImeActionButton())

        // Check that no errors are displayed and button is enabled
        onView(withId(R.id.text_email_error)).check(matches(not(isDisplayed())))
        onView(withId(R.id.text_phone_number_error)).check(matches(not(isDisplayed())))
        onView(withId(R.id.button_submit)).check(matches(isEnabled()))
    }
    @Test
    fun cardHolderData_emptyOptionalFieldsEnableButton() {
        // Create intent with both fields requested
        val intentWithBothFields = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
            putExtra(OmiseActivity.EXTRA_CARD_HOLDER_DATA, CardHolderDataList(arrayListOf(CardHolderDataField.EMAIL, CardHolderDataField.PHONE_NUMBER)))
        }

        scenario = launchActivityForResult(intentWithBothFields)

        // Fill in valid card information but leave card holder fields empty
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeNumberText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"), pressImeActionButton())

        // Check that button is enabled (empty optional fields are valid)
        onView(withId(R.id.button_submit)).check(matches(isEnabled()))
    }
    fun cardHolderData_validCardHolderDataWithInvalidCardInfoDisablesButton() {
        // Create intent with both fields requested
        val intentWithBothFields = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
            putExtra(OmiseActivity.EXTRA_CARD_HOLDER_DATA, CardHolderDataList(arrayListOf(CardHolderDataField.EMAIL, CardHolderDataField.PHONE_NUMBER)))
        }

        scenario = launchActivityForResult(intentWithBothFields)

        // Fill in invalid card information but valid card holder data
        onView(withId(R.id.edit_card_number)).perform(typeText("1234567890")) // Invalid card number
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeNumberText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"))
        onView(withId(R.id.edit_email)).perform(typeText("john.doe@example.com"))
        onView(withId(R.id.edit_phone_number)).perform(typeText("+1234567890"), pressImeActionButton())

        // Check that button is disabled due to invalid card info
        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }
    fun cardHolderData_emailErrorClearsWhenGainingFocus() {
        // Create intent with email field requested
        val intentWithEmail = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
            putExtra(OmiseActivity.EXTRA_CARD_HOLDER_DATA, CardHolderDataList(arrayListOf(CardHolderDataField.EMAIL)))
        }

        scenario = launchActivityForResult(intentWithEmail)

        // Enter invalid email and lose focus to trigger error
        onView(withId(R.id.edit_email)).perform(typeText("invalid-email"))
        onView(withId(R.id.edit_card_number)).perform(click()) // Change focus to trigger validation

        // Verify error is shown
        onView(withId(R.id.text_email_error)).check(matches(isDisplayed()))

        // Focus back on email field
        onView(withId(R.id.edit_email)).perform(click())

        // Verify error is cleared when gaining focus
        onView(withId(R.id.text_email_error)).check(matches(not(isDisplayed())))
    }
    fun cardHolderData_phoneErrorClearsWhenGainingFocus() {
        // Create intent with phone field requested
        val intentWithPhone = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
            putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
            putExtra(OmiseActivity.EXTRA_CARD_HOLDER_DATA, CardHolderDataList(arrayListOf(CardHolderDataField.PHONE_NUMBER)))
        }

        scenario = launchActivityForResult(intentWithPhone)

        // Enter invalid phone and lose focus to trigger error
        onView(withId(R.id.edit_phone_number)).perform(typeText("invalid-phone"))
        onView(withId(R.id.edit_card_number)).perform(click()) // Change focus to trigger validation

        // Verify error is shown
        onView(withId(R.id.text_phone_number_error)).check(matches(isDisplayed()))

        // Focus back on phone field
        onView(withId(R.id.edit_phone_number)).perform(click())

        // Verify error is cleared when gaining focus
        onView(withId(R.id.text_phone_number_error)).check(matches(not(isDisplayed())))
    }
}

private fun typeNumberText(numberText: String): ViewAction =
    object : ViewAction {
        override fun getDescription(): String = "Type number text: $numberText"

        override fun getConstraints(): Matcher<View> =
            allOf(
                isDisplayed(),
                isAssignableFrom(
                    OmiseEditText::class.java,
                ),
            )

        override fun perform(
            uiController: UiController?,
            view: View?,
        ) {
            val editText = view as? OmiseEditText ?: return
            numberText.forEach { editText.append(it.toString()) }
        }
    }
