# Opn Payments Android SDK

[![](https://img.shields.io/maven-central/v/co.omise/omise-android.svg?style=flat-square)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22co.omise%22%20AND%20a%3A%22omise-android%22)
[![](https://img.shields.io/badge/email-support-yellow.svg?style=flat-square)](mailto:support@opn.ooo)
[![Android CI](https://github.com/omise/omise-android/workflows/Android%20CI/badge.svg)](https://github.com/omise/omise-android/actions)

Opn Payments is a payment service provider currently operating in Thailand. Opn Payments provides a set of clean APIs
that help merchants of any size accept cards online.

Opn Payments Android SDK provides Android bindings for the Opn Payments [Token](https://docs.opn.ooo/tokens-api)
and [Source](https://docs.opn.ooo/sources-api) API and components for entering credit card information.

## Security Warning

<div class="notice>
<p>
It is imperative that you use at least the minimum recommended SDK version of `4.3.1` for security reasons. Any version below this poses severe risks of security vulnerabilities, bugs, and unexpected behaviors, which can be detrimental to your application. To avoid these risks and ensure the best user experience, it is highly recommended that you upgrade to the latest supported SDK version. Opn strongly advises the use of version `5.0.0` for superior performance and top-notch security. Don't compromise on the security and performance of your application - upgrade to the latest SDK version today.
</p></div>

## Requirements

- Public key. [Register for an Opn Payments account](https://dashboard.omise.co/signup) to obtain your API keys.
- Android 5.0+ (API 21) target or higher.
- Android Studio and Gradle build system.

## Merchant compliance

**Card data should never transit through your server. We recommend that you follow our guide on how to safely
[collect credit information](https://docs.opn.ooo/collecting-card-information).**

To be authorized to create tokens server-side, you must have a currently valid PCI-DSS
Attestation of Compliance (AoC) delivered by a certified QSA Auditor.

This SDK provides the means to tokenize card data on an end-user mobile phone without the data
having to go through your server.

## Installation

Add the following line to your project's `build.gradle` file inside the `dependencies`
block:

```gradle
implementation 'co.omise:omise-android:4.3.1'
```

## Usage

### Breaking changes in version `v5.0.0`

- **UI Customization Overhaul**: Introducing a new theme-based UI customization approach. Previously customized UI configurations must be updated. This offers configurations for `DefaultTheme`, `DarkTheme`, and `MonoChromeTheme`. See `CheckoutActivity.kt` in the example app for a detailed implementation.
- **Charge Authorization**: A new parameter named `EXTRA_THREE_DS_REQUESTOR_APP_URL` has been introduced and is required for charge authorization. Ensure this parameter is passed during the start of `AuthorizingPaymentActivity` to prevent errors.
- **Automatic Initialization**: The function `initializeAuthoringPaymentConfig()` has been removed and is no longer needed. The SDK now handles initialization automatically. Removed functions:

```kotlin
val threeDSConfig = ThreeDSConfig.Builder()
    .uiCustomization(uiCustomization)
    .timeout(5)
    .build()
val authPaymentConfig = AuthorizingPaymentConfig.Builder()
    .threeDSConfig(threeDSConfig)
    .build()
AuthorizingPaymentConfig.initialize(authPaymentConfig)
```

### Card activity

The simplest way to use this SDK is to integrate the provided `CreditCardActivity`
directly into your application. This activity contains a pre-made credit form and will
automatically [tokenize card
information](https://docs.opn.ooo/security-best-practices) for you.

To use it, first declare the availability of the activity in your `AndroidManifest.xml`
file as follows:

```xml
<activity
  android:name="co.omise.android.ui.CreditCardActivity"
  android:theme="@style/OmiseTheme" />
```

Then, in your activity, declare the method that will start this activity. Launching the activity depends on which version of Android/AGP you are using. Here is a simple example using `registerForActivityResult`:

```kotlin
private val OMISE_PKEY: String = "pkey_test_123"
private val REQUEST_CC: Int = 100

private lateinit var creditCardLauncher: ActivityResultLauncher<Intent>

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    creditCardLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        handleActivityResult(
            CREDIT_CARD_REQUEST_CODE,
            result.resultCode,
            result.data
        )
    }
}

private fun payByCreditCard() {
    Intent(this, CreditCardActivity::class.java).run {
        putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY)
        creditCardLauncher.launch(this)
    }
}
```

Replace the string `pkey_test_123` with the public key obtained from your Opn Payments dashboard.
We discuss the `handleActivityResult` function in the following section.

After the end-user completes entering credit card information, the activity result
callback will be called; handle it as follows:

```kotlin
 private fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    if (resultCode == RESULT_CANCELED) {
        snackbar.setText(R.string.payment_cancelled).show()
        return
    }
}
```

Unless you are working on a project with specific backward compatibility requirements,
we recommend using `registerForActivityResult` to handle activity results in your Android apps.
You can then attach a function to handle the activity result inside the `registerForActivityResult`.
You do not need to set the `requestCode` explicitly in your handle function, but it is included here for simplicity.
You can have a specific function for each `intent` that you launch, and that function will handle the particular
result logic of that intent without the need for the request code.

Several results are returned from the activity. You can obtain them from the
resulting `Intent` with the following code:

- `data.getStringExtra(OmiseActivity.EXTRA_TOKEN)` - The string ID of the token. Use
  this if you only need the ID and not the card data.
- `data.getParcelableExtra(OmiseActivity.EXTRA_TOKEN_OBJECT)` - The full `Token`
  object returned from the Opn Payments API.
- `data.getParcelableExtra(OmiseActivity.EXTRA_CARD_OBJECT)` - The `Card` object
  that is part of the `Token` object returned from the Opn Payments API.

The `getParcelableExtra(key)` function is deprecated and no longer recommended to be used. We advise you to
create your custom function to retrieve the necessary information, as different Android versions may require you to
account for backward compatibility.

### Custom card form

If you need to build your card form, components inside `CreditCardActivity`
can be used on their own. For example, the `CreditCardEditText` can be used in XML as demonstrated:

```xml
<co.omise.android.ui.CreditCardEditText
  android:layout_width="match_parent"
  android:layout_height="wrap_content" />
```

This component provides automatic spacing into groups of 4 digits as the user inputs their card number.
Additionally, the following utility classes are available from the SDK:

- `co.omise.android.ui.CreditCardEditText` - The `CreditCardEditText` class provides utility
  methods for validating and formatting credit card numbers.
- `co.omise.android.ui.CardNameEditText` - The `CardNameEditText` class handles formatting and
  input type for card holder name.
- `co.omise.android.ui.ExpiryDateEditText` - The `ExpiryDateEditText` class handles formatting and
  date range limitation.
- `co.omise.android.ui.SecurityCodeEditText` - The `SecurityCodeEditText` class handles formatting
  and input type for security code.

### Manual tokenization

If you have built your card or Google Pay form, you can use the SDK to tokenize the
card manually. First, build the `Client` and supply your public key as follows:

```kotlin
private val client = Client("pkey_test_123")
```

Then, construct the token request with values from your custom form:

```kotlin
// Sample builder for credit card
val cardParam = CardParam(
                name = "JOHN Doe",
                number = "4242424242424242",
                expirationMonth = 10,
                expirationYear = 2020,
                securityCode = "123"
)

val request = Token.CreateTokenRequestBuilder(cardParam).build()

// Sample builder for Google Pay
val tokenizationParam = TokenizationParam(
                method = "googlepay",
                data = "{\"signature\":\"MEQCIA+wGZttxT13yz599zQjYugoz5kClNSmVa39vKv6ZOenAiARRtHQ0aYSrfd3oWhB\/ZtEeJs3ilT\/J0pYz1EWnzU2fw\\u003d\\u003d\",\"intermediateSigningKey\":{\"signedKey\":\"{\\\"keyValue\\\":\\\"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEev+pVoUgtoS+y8Ecz3c72OFBD3d74XJOcnRxVmCV+2TJTW1g4d0UhDkhHeURhHQNvJPyBFHfYIUUj\/EYhYAzgQ\\\\u003d\\\\u003d\\\",\\\"keyExpiration\\\":\\\"1647856171825\\\"}\",\"signatures\":[\"MEYCIQClXfVcil7qaG2btVbyzf6x1\/MqCTbbJM\/tGN4iME4M9wIhANL53daWJHdDPpKxR3M\/Jis4WPVb093PW7fChj\/gCQUS\"]},\"protocolVersion\":\"ECv2\",\"signedMessage\":\"{\\\"encryptedMessage\\\":\\\"4JighTc0b1HhRQu+NgQN1XQWWOeB4YyR5cMFi8Vu3FeWHAjPtGs3LjrdpWhJhWekURzD6BZCbg1xakYvAMsahoTyUzDLtNpKmlglFpVjBSSYkPKFT6xovTKsWS7xC\/x9AvJsATtotwN8TTiP3+1dXtLLFClnCTkg9vEvChvXq0FwnrUOBtMiWukBY84R2rpzqNuZoh6gdvWHgPP6RczhtERg+kqKdd4\/UnKE8ElzOWYDmZoJvFhxU\/O97vHW1ohOe8ut94bxiPH6DB82Ec87Mu\/oArsGMpsnFVsWzIcLX+q+KayGRbKxPQzV726fO7GipG94KiF7YfCk1r+D+jkFR7x0ev6l+XRoTz+PKIlhrcn3DEYJudJAP\/Xh2kj\/csnLn4XdKV0aZ5Ua3IauA4fQl80pAo9foujiRGwagHHOfnp6iMjA\/CdG9SNQS3eUdsxtlJKPoK4rtv7cwISNQvoCWMv748YvV3f+LEOWf8couRgrxPCPbk1vO8TfNOgSAjULzRs+C1xy6\/j5aZU46PpomEClDWrujMAcDVqCnExTx2QE9IAb4n02V6UxWv8Dgqv5TsRKjPe7WSCO0+jRWAvs6wBBUbFPHvEe4do+rQ\\\\u003d\\\\u003d\\\",\\\"ephemeralPublicKey\\\":\\\"BGJhfH3jWMmZtIALmYr7fWxYSNSCFoAT9MCOcbCZdO3LmP6njpGk9LISmr+H1Wk9XUZuMvNQmMHE+yFzW\/sA5lg\\\\u003d\\\",\\\"tag\\\":\\\"d9a6aVaoIEQm+bTjd5M2HL7+OeIup0Jb6rM1CN7v3NQ\\\\u003d\\\"}\"}",

                // Add your billing information here (optional)
                billing_name = "John Doe",
                billing_street1 = "1600 Amphitheatre Parkway"
)

val request = Token.CreateTokenRequestBuilder(tokenization = tokenizationParam).build()
```

Then send the request using the `client` that you have constructed earlier:

```kotlin
client.send(request, object : RequestListener<Token>{
   override fun onRequestSucceed(model: Token) {
      // you've got a Token!
   }

    override fun onRequestFailed(throwable: Throwable) {
      // something bad happened
    }
})
```

The `Client` class will automatically dispatch the network call on an internal background
thread, and will call listener methods on the thread that initially calls the `send`
method.

### Payment creator activity

Another way to use the Opn Payments Android SDK is to integrate the `PaymentCreatorActivity`
to allow users to create a payment source from the list of sources available for the account.

To use it, first declare the availability of the activity in your `AndroidManifest.xml` file as follows:

```xml
<activity
  android:name="co.omise.android.ui.PaymentCreatorActivity"
  android:theme="@style/OmiseTheme" />
```

Then in your activity, declare the method that will start this activity as follows:

```kotlin
private val OMISE_PKEY: String = "pkey_test_123"
private val REQUEST_CC: Int = 100
private lateinit var paymentCreatorLauncher: ActivityResultLauncher<Intent>

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    paymentCreatorLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        handleActivityResult(
            PAYMENT_CREATOR_REQUEST_CODE,
            result.resultCode,
            result.data
        )
    }
}

private fun showPaymentCreatorActivity() {
    Intent(this, PaymentCreatorActivity::class.java).run {
        putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY)
        putExtra(OmiseActivity.EXTRA_AMOUNT, 150000L)
        putExtra(OmiseActivity.EXTRA_CURRENCY, "thb")

        //You can retrieve your account's capabilities through the SDK (will be explained below)
        putExtra(OmiseActivity.EXTRA_CAPABILITY, capability)

        paymentCreatorLauncher.launch(this)
    }
}
```

Replace the string `pkey_test_123` with the public key obtained from your Opn Payments dashboard.

Declare a `capability` variable as a `Capability` object and pass it as the value for the `OmiseActivity.EXTRA_CAPABILITY` key for your `Intent`. This way, the `PaymentCreatorActivity` will display the payment methods in the `Capability` object.

There are two options to retrieve the Capability object.

1. You can retrieve the Capability object from your account's capabilities through the [Retrieve Capabilities](#retrieve-capabilities) function.

2. You can also create a `Capability` object to create your own capabilities using the helper function `Capability.create()`.

   **Here is the sample:**

   ```kotlin
   val capability = Capability.create(
           allowCreditCard = true,
           sourceTypes = listOf(SourceType.PromptPay, SourceType.TrueMoney)
   )
   ```

   > **Note**
   > Ensure you are adding payment methods supported by the account.
   > If not, you won't be able to create a source to continue the payment process.

After the end user selects and creates a payment source, the activity result callback will be called; handle it as follows:

```kotlin
private fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    if (resultCode == RESULT_CANCELED) {
        snackbar.setText(R.string.payment_cancelled).show()
        return
    }

    when (requestCode) {
        PAYMENT_CREATOR_REQUEST_CODE -> {
            if (data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)) {
                val source = data.parcelable<Source>(OmiseActivity.EXTRA_SOURCE_OBJECT)
                snackbar.setText(source?.id ?: "No source object.").show()
                Log.d(TAG, "source: ${source?.id}")
            } else if (data.hasExtra(OmiseActivity.EXTRA_TOKEN)) {
                val token = data.parcelable<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
                snackbar.setText(token?.id ?: "No token object.").show()
                Log.d(TAG, "token: ${token?.id}")
            }
        }
    }
}
```

Two different results that could be returned are:

- `data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)` - The `Source` object created by the payment creator.
- `data.hasExtra(OmiseActivity.EXTRA_TOKEN)` - The `Token` object created in case the payment source created was a card.

### Google Pay activity

We support GooglePay as a tokenization method in our payment gateway. This activity contains a pre-made `Pay with Google Pay` button and will automatically [tokenize the Google Pay token](https://docs.opn.ooo/security-best-practices) for you.

To use it, first declare the availability of the activity in your `AndroidManifest.xml`
file as follows:

```xml
<activity
  android:name="co.omise.android.ui.GooglePayActivity"
  android:theme="@style/OmiseTheme" />
```

Then in your activity, declare the method that will start this activity as follows:

```kotlin
private val OMISE_PKEY: String = "pkey_test_123"
private val amount: Long = 3000
private val currency: String = "THB"
private val cardBrands: ArrayList<String> = arrayListOf("Visa", "Mastercard")
private val googlepayMerchantId: String = "merchant_123"
private val googlepayRequestBillingAddress: Boolean = false
private val googlepayRequestPhoneNumber: Boolean = false

private val REQUEST_GPAY: Int = 100

override fun navigateToGooglePayForm() {
    val intent = Intent(activity, GooglePayActivity::class.java).apply {
        putExtra(EXTRA_PKEY, OMISE_PKEY)
        putExtra(EXTRA_AMOUNT, amount)
        putExtra(EXTRA_CURRENCY, currency)
        putStringArrayListExtra(EXTRA_CARD_BRANDS, cardBrands)
        putExtra(EXTRA_GOOGLEPAY_MERCHANT_ID, googlepayMerchantId)
        putExtra(EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS, googlepayRequestBillingAddress)
        putEXTRA(EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER, googlepayRequestPhoneNumber)
    }
    gPayLauncher.launch(this)
}
```

- Replace the `OMISE_PKEY` with your Opn Payments public key obtained from our dashboard.
- Replace the `amount` with the amount you want to charge in subunits.
- Replace the `currency` with your currency in the ISO 4217 format.
- Replace the `cardBrands` with the list from our [capability API](https://docs.opn.ooo/capability-api) or leave it blank to use default values.
- Replace the `googlepayMerchantId` with your [Google Pay merchant ID](https://developers.google.com/pay/api/web/guides/setup) (not needed in test mode).
- Set the `googlepayRequestBillingAddress` to `true` to attach the cardholder's name and billing address to the token.
- When the cardholder's billing address is requested, set the `googlepayRequestPhoneNumber` to `true` to also attach the cardholder's phone number to the token.

#### Return values

Several results are returned from the activity. You can obtain them from the
resulting `Intent` with the following code:

- `data.getStringExtra(OmiseActivity.EXTRA_TOKEN)` - The string ID of the token. Use
  this if you only need the ID and not the card data.
- `data.getParcelableExtra(OmiseActivity.EXTRA_TOKEN_OBJECT)` - The full `Token`
  object returned from the Opn Payments API.
- `data.getParcelableExtra(OmiseActivity.EXTRA_CARD_OBJECT)` - The `Card` object
  that is part of the `Token` object returned from the Opn Payments API.

#### Using your activity

You can use your activity if you prefer. We recommend that you follow [Google's tutorial and guidelines](https://developers.google.com/pay/api/android/overview) and make sure
that you follow their [brand guidelines](https://developers.google.com/pay/api/android/guides/brand-guidelines) as well.

You can use our Google Pay request builder `request/GooglePay.kt`, which includes request builders that you can use to request the Google Pay token.
Configurations for the builders are modifiable through the `class` constructor to suit your needs. However, you are also welcome to make your own integration and call
our tokens builder yourself.

### Creating a source

If you need to create a payment source and use it outside the provided SDK context, follow these steps. First, build the Client and supply your public key in this manner:

```kotlin
private val client = Client("pkey_test_123")
```

Then, construct the Source request:

```kotlin
val request = Source.CreateSourceRequestBuilder(25000L, "thb", SourceType.Installment.Bay)
      .description("Item")
      .email("e@mail.com")
      .storeId("id-123")
      .storeName("Store")
      .phoneNumber("06207658854")
      .installmentTerm(3)
      .build()
```

Then, send the request using the `Client` you have constructed earlier. You will get a `Source` object in response:

```kotlin
client.send(request, object : RequestListener<Source>{
   override fun onRequestSucceed(model: Source) {
      // you've got a Source!
   }

    override fun onRequestFailed(throwable: Throwable) {
      // something bad happened
    }
})
```

The `Client` class will automatically dispatch the network call on an internal background thread and call listener methods on the thread that initially calls the `send` method.

### Retrieving capabilities

You can retrieve your capabilities and available payment sources through the SDK in the following manner.

First build the Client and supply your public key this way:

```kotlin
private val client = Client("pkey_test_123")
```

Then, construct the `Capability` request:

```kotlin
val request = Capability.GetCapabilitiesRequestBuilder().build()
```

And then send the request using the Client that you constructed earlier:

```kotlin
client.send(request, object : RequestListener<Capability> {
   override fun onRequestSucceed(model: Capability) {
        // you have capabilities!
   }

   override fun onRequestFailed(throwable: Throwable) {
         // something bad happened
   }
})
```

The `Client` class will automatically dispatch the network call on an internal background thread and call listener methods on the thread that initially calls the `send` method.

### Customizing themes

If you wish to customize the elements on the `CreditCardActivity` to
match your application's branding, you can do so by overriding the following styles
as shown in the following snippet:

AndroidManifest.xml

```xml
<activity
  android:name="co.omise.android.ui.CreditCardActivity"
  android:theme="@style/SampleTheme" />
```

style.xml

```xml
<resources>
    <style name="SampleTheme" parent="Theme.MaterialComponents">
        <item name="android:editTextStyle">@style/SampleEditText</item>
        <item name="materialButtonStyle">@style/SampleButton</item>
        <item name="editTextLabelStyle">@style/SampleEditTextLabel</item>
        <item name="editTextErrorStyle">@style/SampleEditTextError</item>
    </style>

    <style name="SampleEditText" parent="Widget.AppCompat.EditText">
        <item name="android:textColor">#FFFFFFFF</item>
        <item name="android:textColorHint">#B3FFFFFF</item>
        <item name="android:textSize">12sp</item>
        <item name="backgroundTint">#FFFFFFFF</item>
    </style>

    <style name="SampleEditTextLabel">
        <item name="android:textColor">#B3FFFFFF</item>
        <item name="android:textAppearance">@style/TextAppearance.AppCompat.Body1</item>
    </style>

    <style name="SampleEditTextError">
        <item name="android:textColor">#FFFF0000</item>
    </style>

    <style name="SampleButton" parent="Widget.MaterialComponents.Button">
        <item name="backgroundTint">#FFFFFFFF</item>
        <item name="android:textColor">#FF000000</item>
    </style>
</resources>

```

And if you choose to customize the item text sizes for the lists in `PaymentCreatorActivity`, you
can do so by overriding the following style.

AndroidManifest.xml

```xml
<activity
  android:name="co.omise.android.ui.PaymentCreatorActivity"
  android:theme="@style/SampleTheme" />
```

style.xml

```xml
<resources>
    <style name="SampleTheme" parent="Theme.MaterialComponents">
        ...
        <item name="android:itemTextAppearance">@style/SampleItemTextAppearance</item>
    </style>

    <style name="SampleItemTextAppearance" parent="TextAppearance.AppCompat.Body1">
        <item name="android:textSize">16sp</item>
        <item name="android:textColor">#FFFFFF</item>
    </style>
</resources>
```

## Authorizing payment

Some payment methods require the customer to authorize the payment using an authorization URL. This includes [3-D Secure verification](https://docs.opn.ooo/fraud-protection#3-d-secure), [Internet Banking payment](https://docs.opn.ooo/internet-banking), [Mobile Banking SCB](https://docs.opn.ooo/mobile-banking-scb), etc. Opn Payments Android SDK provides a built-in class to handle the authorization.

On payment methods that require opening the external app (e.g., mobile banking app) to authorize the transaction, set the _return_uri_ to a **deep link** or **app link** to be able to open the merchant app. Otherwise, after the cardholder authorizes the transaction on the external app, the flow redirects to the normal link in the _return_uri_, and opens it on the browser app, resulting in the payment not being completed.
Some authorized URLs will be processed using the in-app browser flow, and others will be processed using the native flow from the SDK (3DS v2), and the SDK automatically handles all of this.

### Authorizing payment activity

To use it, first declare the availability of the activity in your `AndroidManifest.xml`
file as follows:

```xml
<activity
  android:name="co.omise.android.ui.AuthorizingPaymentActivity"
  android:theme="@style/OmiseTheme" />
```

Then in your activity, declare the method that will start this activity as follows:

```kotlin
private fun startAuthoringPaymentActivity() {
    Intent(this, AuthorizingPaymentActivity::class.java).run {
            putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeUrl)
            putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            putExtra(EXTRA_UI_CUSTOMIZATION, uiCustomization)
            putExtra(
                EXTRA_THREE_DS_REQUESTOR_APP_URL,
                "sampleapp://omise.co/authorize_return"
            )
            authorizingPaymentLauncher.launch(this)
        }
}
```

Replace the string `EXTRA_AUTHORIZED_URLSTRING` with the authorized URL that comes with the created charge and the array of string `EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS` with the expected pattern of redirected URLs array.
Replace the string `EXTRA_THREE_DS_REQUESTOR_APP_URL` with the url of your app to allow the external bank apps to navigate back to your app when required.
The `EXTRA_UI_CUSTOMIZATION` parameter is used to customize the UI of the built-in 3DS SDK during the 3DS challenge flow.
If you want to customize the title of the authorizing payment activity, you must use the theme customization and pass the `headerText` in the `toolbarCustomization` in the `DEFAULT` theme parameter:

```kotlin
val toolbarCustomization = ToolbarCustomizationBuilder()
            .textFontName("font/roboto_mono_bold.ttf")
            .textColor("#000000")
            .textFontSize(20)
            .backgroundColor("#FFFFFF")
            .headerText("Secure Checkout")
            .buttonText("Close")
            .build()

            val uiCustomization = UiCustomizationBuilder()
            .setDefaultTheme(ThemeConfig(
                toolbarCustomization = toolbarCustomization,
            ))
            .build()
```

You can check out the [UiCustomization](/sdk/src/main/java/co/omise/android/config/UiCustomization.kt) class to see customizable UI elements in the challenge flow.

After the end-user completes the payment authorization process, the activity result
callback will be sent, and you will receive different responses based on how the transaction was processed
and which flow it used. Handle it in this manner:

```kotlin
 fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    // custom result code when web view is closed
    if (resultCode == AuthorizingPaymentActivity.WEBVIEW_CLOSED_RESULT_CODE) {
        snackbar.setText(R.string.webview_closed).show()
        return
    }

    if (resultCode == RESULT_CANCELED) {
        snackbar.setText(R.string.payment_cancelled).show()
        return
    }

    if (data == null) {
        snackbar.setText(R.string.payment_success_but_no_result).show()
        return
    }

    when (requestCode) {
        AUTHORIZING_PAYMENT_REQUEST_CODE -> {
            with(data.parcelable<AuthorizingPaymentResult>(AuthorizingPaymentActivity.EXTRA_AUTHORIZING_PAYMENT_RESULT)) {
                Log.d(TAG, this.toString())
                val resultMessage = when (this) {
                    is AuthorizingPaymentResult.ThreeDS1Completed -> "Authorization with 3D Secure version 1 completed: returnedUrl=${returnedUrl}"
                    is AuthorizingPaymentResult.ThreeDS2Completed -> "Authorization with 3D Secure version 2 completed: transStatus=${transStatus}"
                    is AuthorizingPaymentResult.Failure -> {
                        Log.e(TAG, throwable.message, throwable.cause)
                        throwable.message ?: "Unknown error."
                    }

                    null -> "Authorization result not found"
                }
                Log.d(TAG, resultMessage)
                snackbar.setText(resultMessage).show()
            }
        }
    }
}
```

You can check out the sample implementation in the [CheckoutActivity](./app/src/kotlin/java/co/omise/android/example/CheckoutActivity.kt) class in the sample app.

### Observing charge status in the token

The following utility function observes the token until its charge status changes. You can use it to check the charge status after the payment authorization process is completed.

```kotlin
val client = Client("pkey_test_1234")
client.observeTokenUntilChargeStatusChanged("tokn_test_1234", object: RequestListener<Token> {
    override fun onRequestSucceed(model: Token) {
        TODO("Not yet implemented")
    }

    override fun onRequestFailed(throwable: Throwable) {
        TODO("Not yet implemented")
    }
})
```

### Authorizing payment via an external app

Some request methods allow the user to authorize the payment with an external app, for example Alipay. When a user needs to authorize the payment with an external app, `AuthorizingPaymentActivity` will automatically open an external app. However, merchant developers must handle the `Intent` callback themselves.

## ProGuard rules

If you enable ProGuard, then add these rules to your ProGuard file.

```ProGuard
-dontwarn okio.**
-dontwarn com.google.common.**
-dontwarn org.joda.time.**
-dontwarn javax.annotation.**
-dontwarn com.squareup.**

-keep class co.omise.android.** { *; }
-keep class com.nimbusds.jose.** { *; }
```

## Protecting screenshot and screen recording

**Omise Android SDK** has built-in protection against screenshots and screen recording. To turn off this feature, you can pass `OmiseActivity.EXTRA_IS_SECURE` with a value of `false` when starting the following activities: `CreditCardActivity`, `PaymentCreatorActivity`, and `AuthorizingPaymentActivity`.

## Contributing

Pull requests and bug fixes are welcome.

## License

MIT [See the full license text](https://github.com/omise/omise-android/blob/master/LICENSE)
