# Omise Android SDK

[![](https://img.shields.io/maven-central/v/co.omise/omise-android.svg?style=flat-square)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22co.omise%22%20AND%20a%3A%22omise-android%22)
[![](https://img.shields.io/badge/email-support-yellow.svg?style=flat-square)](mailto:support@omise.co)

Omise is a payment service provider currently operating in Thailand. Omise provides a set of clean APIs
that help merchants of any size accept credit cards online.

Omise Android SDK provides Android bindings for the Omise [Token](https://www.omise.co/tokens-api)
and [Source](https://www.omise.co/sources-api) API as well as components for entering credit card information.

Hop into our forum (click the badge above) or email our support team if you have any questions
 regarding this SDK and the functionality it provides.

## Requirements

* Public key. [Register for an Omise account](https://dashboard.omise.co/signup) to obtain your API keys.
* Android 5.0+ (API 21) target or higher.
* Android Studio and Gradle build system.

## Merchant Compliance

**Card data should never transit through your server. We recommend that you follow our guide on how to safely
[collect credit information](https://www.omise.co/collecting-card-information).**

To be authorized to create tokens server-side you must have a currently valid PCI-DSS
Attestation of Compliance (AoC) delivered by a certified QSA Auditor.

This SDK provides means to tokenize card data on end-user mobile phone without the data
having to go through your server.

## Installation

Add the following line to your project's build.gradle file inside the `dependencies`
block:

```gradle
implementation 'co.omise:omise-android:3.2.+'
```

## Usage

### Credit Card activity

The simplest way to use this SDK is to integrate the provided `CreditCardActivity`
directly into your application. This activity contains a pre-made credit form and will
automatically [tokenize credit card
information](https://www.omise.co/security-best-practices) for you.

To use it, first declare the availability of the activity in your `AndroidManifest.xml`
file as follows:

```xml
<activity
  android:name="co.omise.android.ui.CreditCardActivity"
  android:theme="@style/OmiseTheme" />
```

Then in your activity, declare the method that will start this activity as follows:

```kotlin
private val OMISE_PKEY: String = "pkey_test_123"
private val REQUEST_CC: Int = 100

private fun showCreditCardForm() {
    val intent = Intent(this, CreditCardActivity::class.java)
    intent.putExtra(OmiseActivity.EXTRA_PKEY, OMISE_PKEY)
    startActivityForResult(intent, REQUEST_CC)
}
```

Replace the string `pkey_test_123` with the public key obtained from your Omise dashboard.

After the end-user completes entering credit card information, the activity result
callback will be called, handle it like so:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == RESULT_CANCELED) {
        // handle the cancellation
        return
    }

    if (requestCode == REQUEST_CC) {
        val token = data?.getParcelableExtra<Token>(EXTRA_TOKEN_OBJECT)
        // process your token here
    }
}
```

A number of results are returned from the activity. You can obtain them from the
resulting `Intent` with the following code:

* `data.getStringExtra(OmiseActivity.EXTRA_TOKEN)` - The string ID of the token. Use
  this if you only needs the ID and not   the card data.
* `data.getParcelableExtra(OmiseActivity.EXTRA_TOKEN_OBJECT)` - The full `Token`
  object returned from the Omise API.
* `data.getParcelableExtra(OmiseActivity.EXTRA_CARD_OBJECT)` - The `Card` object
  which is part of the `Token` object returned from the Omise API.

### Custom Credit Card Form

If you need to build your own credit card form, components inside `CreditCardActivity`
can be used on their own. For example, the `CreditCardEditText` can be used in XML in this way:

```xml
<co.omise.android.ui.CreditCardEditText
  android:layout_width="match_parent"
  android:layout_height="wrap_content" />
```

This component provides automatic spacing into groups of 4 digits as the user inputs their credit card number.
Additionally the following utility classes are available from the SDK:

* `co.omise.android.ui.CreditCardEditText` - The `CreditCardEditText` class provides utility
   methods for validating and formatting credit card numbers.
* `co.omise.android.ui.CardNameEditText` - The `CardNameEditText` class handles formatting and
   input type for card holder name.
* `co.omise.android.ui.ExpiryDateEditText` - The `ExpiryDateEditText` class handles formatting and
  date range limitation.
* `co.omise.android.ui.SecurityCodeEditText` - The `SecurityCodeEditText` class handles formatting
   and input type for security code.

### Manual Tokenization

If you have built your own credit card form, you can use the SDK to manually tokenize the
card. First build the `Client` and supply your public key this way:

```kotlin
private val client = Client("pkey_test_123")
```

Then construct the token request with values from your custom form:

```kotlin
val cardParam = CardParam(
                name = "JOHN Doe",
                number = "4242424242424242",
                expirationMonth = 10,
                expirationYear = 2020,
                securityCode = "123")

val request = Token.CreateTokenRequestBuilder(cardParam).build()
```

And then send the request using the `client` you have constructed earlier:

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
thread and will call listener methods on the thread that initially calls the `send`
method.

### Payment Creator activity
Another way to use the Omise Android SDK is to integrate the `PaymentCreatorActivity` 
to allow users to create a payment source from the list of sources available for the account.

To use it, first declare the availability of the activity in your AndroidManifest.xml file as follows:

```xml
<activity
  android:name="co.omise.android.ui.PaymentCreatorActivity"
  android:theme="@style/OmiseTheme" />
```

Then in your activity, declare the method that will start this activity as follows:

```kotlin
private val OMISE_PKEY: String = "pkey_test_123"
private val REQUEST_CC: Int = 100

private fun showPaymentCreatorActivity() {
    val intent = Intent(this@CheckoutActivity, PaymentCreatorActivity::class.java)
    intent.putExtra(OmiseActivity.EXTRA_PKEY, OMISE_PKEY)
    intent.putExtra(OmiseActivity.EXTRA_AMOUNT, 150000L)
    intent.putExtra(OmiseActivity.EXTRA_CURRENCY, "thb")

    // you can retrieve your account's capabilities through the SDK (will be explained below)
    intent.putExtra(OmiseActivity.EXTRA_CAPABILITY, capability)

    startActivityForResult(intent, REQUEST_CC)
}
```

Replace the string `pkey_test_123` with the public key obtained from your Omise dashboard.

Declare a `capability` variable as a `Capability` object and pass it as the value for the `OmiseActivity.EXTRA_CAPABILITY` key for your `Intent`. This way, the `PaymentCreatorActivity` will display the payment methods contained in the `Capability` object.  

There are 2 options to retrieve the Capability object. 

1. You can retrieve the Capability object from your account's capabilities through the [Capability](#retrieve-capabilities). 

2. Or you can create a `Capability` object to create your own capabilities using the helper function `Capability.create()`.  

    **Here is the sample:**

    ```kotlin
    val capability = Capability.create(
            allowCreditCard = true,
            sourceTypes = listOf(SourceType.PromptPay, SourceType.TrueMoney)
    )
    ```

    > **Note**
    > Ensure you are adding payment methods supported by the account. 
    > If not, you won't be able to create a source to continue the payment process..

After the end user selects and creates a payment source, the activity result callback will be called; handle it as follows:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == RESULT_CANCELED) {
        // handle the cancellation
        return
    }

    if (requestCode == REQUEST_CC) {
        if (data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)) {
            val source = data?.getParcelableExtra<Source>(OmiseActivity.EXTRA_SOURCE_OBJECT)
            // process the source here
        } else if (data.hasExtra(OmiseActivity.EXTRA_TOKEN)) {
            val token = data?.getParcelableExtra<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
            // process the token here
        }
    }
}
```

Two different results that could be returned are:

* `data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)` - The `Source` object created by the payment creator.
* `data.hasExtra(OmiseActivity.EXTRA_TOKEN)` - The `Token` object created in case the payment source created was a credit card.

### Creating a source
If you need to create a payment source on your own and use it outside of the provided SDK context, you can do follow these steps. First build the Client and supply your public key in this manner:

```kotlin
private val client = Client("pkey_test_123")
```

Then construct the Source request:

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

And then send the request using the `client` you have constructed earlier and you will get a `Source` object in response:

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

The `Client` class will automatically dispatch the network call on an internal background thread and will call listener methods on the thread that initially calls the send method.

### Retrieve Capabilities
You can retrieve all of your capabilities and available payment sources through the SDK in the following manner.

First build the Client and supply your public key this way:

```kotlin
private val client = Client("pkey_test_123")
```

Then construct the Capability request:

```kotlin
val request = Capability.GetCapabilitiesRequestBuilder().build()
```

And then send the request using the client we constructed earlier:

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

The `Client` class will automatically dispatch the network call on an internal background thread and will call listener methods on the thread that initially calls the send method.

### Theme customization
If you wish to customize the elements on the `CreditCardActivity` in order to
match your application's branding, you can do so by overriding the following styles
as shown in the snippet below:

AndroidManifest.xml
```xml
<activity
  android:name="co.omise.android.ui.CreditCardActivity"
  android:theme="@style/SampleTheme" />
```

style.xml
```xml
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
    <style name="SampleTheme" parent="Theme.MaterialComponents">
        ...
        <item name="android:itemTextAppearance">@style/SampleItemTextAppearance</item>
    </style>
    
    <style name="SampleItemTextAppearance" parent="TextAppearance.AppCompat.Body1">
        <item name="android:textSize">16sp</item>
        <item name="android:textColor">#FFFFFF</item>
    </style>
```

## Authorizing Payment
Some payment methods require the customer to authorize the payment via an authorization URL. This includes the [3-D Secure verification](https://www.omise.co/fraud-protection#3-d-secure), [Internet Banking payment](https://www.omise.co/offsite-payment), [Alipay](https://www.omise.co/alipay), etc. Omise Android SDK provides a built in class to handle the authorization.


### Authorizing Payment activity

To use it, first declare the availability of the activity in your `AndroidManifest.xml`
file as follows:

```xml
<activity
  android:name="co.omise.android.ui.AuthorizingPaymentActivity"
  android:theme="@style/OmiseTheme" />
```

Then in your activity, declare the method that will start this activity this way:

```kotlin
private fun showAuthorizingPaymentForm() {
    val intent = Intent(this, AuthorizingPaymentActivity::class.java)
    intent.putExtra(AuthorizingPaymentURLVerifier.EXTRA_AUTHORIZED_URLSTRING, AUTHORIZED_URL)
    intent.putExtra(AuthorizingPaymentURLVerifier.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, EXPECTED_URL_PATTERNS)
    startActivityForResult(intent, AUTHORIZING_PAYMENT_REQUEST_CODE)
}
```

Replace the string `AUTHORIZED_URL` with the authorized URL that comes with the created charge and the array of string `EXPECTED_URL_PATTERNS` with the expected pattern of redirected URLs array.

After the end-user completes the authorizing payment process, the activity result
callback will be called, handle it in this manner:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == AUTHORIZING_PAYMENT_REQUEST_CODE && resultCode == RESULT_OK) {
        val url = data?.getStringExtra(AuthorizingPaymentURLVerifier.EXTRA_RETURNED_URLSTRING)
        // Use the redirected URL here
    }
}
```

### Authorizing Payment via an external app

Some request methods allow the user to authorize the payment with an external app, for example Alipay. When a user would like to authorize the payment with an external app, `AuthorizingPaymentActivity` will automatically open an external app. However merchant developers must handle the `Intent` callback by themselves.

### 3D Secure 2

To support 3D Secure 2, you can check out the [3D Secure guide](docs/3d-secure-v2.md).

## ProGuard Rules

If you enable ProGuard, then add this rules in your ProGuard file.

```ProGuard
-dontwarn okio.**
-dontwarn com.google.common.**
-dontwarn org.joda.time.**
-dontwarn javax.annotation.**
-dontwarn com.squareup.**
```

## Contributing

Pull requests and bug fixes are welcome. For larger scope of work, please pop on to our [forum](https://forum.omise.co) to discuss first.

## LICENSE

MIT [See the full license text](https://github.com/omise/omise-android/blob/master/LICENSE)
