# Omise Android SDK

[![](https://img.shields.io/maven-central/v/co.omise/omise-android.svg?style=flat-square)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22co.omise%22%20AND%20a%3A%22omise-android%22)
[![](https://img.shields.io/badge/email-support-yellow.svg?style=flat-square)](mailto:support@omise.co)
[![](https://img.shields.io/badge/discourse-forum-1a53f0.svg?style=flat-square&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAAAXNSR0IArs4c6QAAAAlwSFlzAAALEwAACxMBAJqcGAAAAVlpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IlhNUCBDb3JlIDUuNC4wIj4KICAgPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICAgICAgPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIKICAgICAgICAgICAgeG1sbnM6dGlmZj0iaHR0cDovL25zLmFkb2JlLmNvbS90aWZmLzEuMC8iPgogICAgICAgICA8dGlmZjpPcmllbnRhdGlvbj4xPC90aWZmOk9yaWVudGF0aW9uPgogICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkRGPgo8L3g6eG1wbWV0YT4KTMInWQAAAqlJREFUKBU9UVtLVFEU%2FvY%2B27mPtxl1dG7HbNRx0rwgFhJBPohBL9JTZfRQ0YO9RU%2FVL6iHCIKelaCXqIewl4gEBbEyxSGxzKkR8TbemmbmnDlzVvsYtOHbey1Y317fWh8DwCVMCfSHww3ElCs7CjuzbOcNIaEo9SbtlDRjZiNPY%2BvrqSWrTh7l3yPvrmh0KBZW59HcREjEqcGpElAuESRxopU648dTwfrIyH%2BCFXSH1cFgJLqHlma6443SG0CfqYY2NZjQnkV8eiMgP6ijjnizHglErlocdl5VA0mT3v102dseL2W14cYM99%2B9XGY%2FlQArd8Mo6JhbSJUePHytvf2UdnW0qen93cKQ4nWXX1%2FyOkZufsuZN0L7PPzkthDDZ4FQLajSA6XWR8HWIK861sCfj68ggGwl83mzfMclBmAQ%2BktrqBu9wOhcD%2BB0ErSiFFyEkdcYhKD27mal9%2F5FY36b4BB%2FTvO8XdQhlUe11F3WG2fc7QLlC8wai3MGGQCGDkcZQyymCqAPSmati3s45ygWseeqADwuWS%2F3wGS5hClDMMstxvJFHQuGU26yHsY6iHtL0sIaOyZzB9hZz0hHZW71kySSl6LIJlSgj5s5LO6VG53aFgpOfOFCyoFmYsOS5HZIaxVwKYsLSbJJn2kfU%2BlNdms5WMLqQRklX0FX26eFRnKYwzX0XRsgR0uUrWxplM7oqPIq8r8cZrdLNLqaABayxZMTTx2HVfglbP4xkcvqZEMNfmglevRi1ny5mGfJfTuQiBEq%2FMBvG0NqDh2TY47sbtJAuO%2Fe9%2Fn3STRFosm2WIxsFSFrFUfwHb11JNBNcaZSp8yb%2FEhHW3suWRNZRzDGvxb0oifk5lmnX2V2J2dEJkX1Q0baZ1MvYXPXHvhAga7x9PTEyj8a%2BF%2BXbxiTn78bSQAAAABJRU5ErkJggg%3D%3D)](https://forum.omise.co)


Omise is a payment service provider currently operating in Thailand. Omise provides a set
of clean APIs that helps merchants of any size accept credit cards online.

Omise Android SDK provides Android bindings for the Omise
[Tokenization](https://www.omise.co/tokens-api) API so you do not need to pass credit card
data to your server as well as components for entering credit card information.

Hop into our forum (click the badge above) or email our support team if you have any
question regarding this SDK and the functionality it provides.

## Requirements

* Public key. [Register for an Omise account](https://dashboard.omise.co/signup) to obtain your API keys.
* Android 4.1+ (API 16) target or higher.
* Android Studio and Gradle build system.

## Merchant Compliance

**Card data should never transit through your server. We recommend that you follow our
guide on how to safely
[collect credit information](https://www.omise.co/collecting-card-information).**

To be authorized to create tokens server-side you must have a currently valid PCI-DSS
Attestation of Compliance (AoC) delivered by a certified QSA Auditor.

This SDK provides means to tokenize card data on end-user mobile phone without the data
having to go through your server.

## Installation

Adds the following line to your project's build.gradle file inside the `dependencies`
block:

```groovy
compile 'co.omise:omise-android:3.0.0'
```

## Usage

#### Credit Card activity

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

```java
private static final String OMISE_PKEY = "pkey_test_123";
private static final int REQUEST_CC = 100;

private void showCreditCardForm() {
  Intent intent = new Intent(this, CreditCardActivity.class);
  intent.putExtra(CreditCardActivity.EXTRA_PKEY, OMISE_PKEY);
  startActivityForResult(intent, REQUEST_CC);
}
```

Replace the string `pkey_test_123` with the public key obtained from your Omise dashboard.

After the end-user completes entering credit card information, the activity result
callback will be called, handle it like so:

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  switch (requestCode) {
    case REQUEST_CC:
      if (resultCode == RESULT_CANCELED) {
        return;
      }

      Token token = data.getParcelableExtra(OmiseActivity.EXTRA_TOKEN_OBJECT);
      // process your token here.

    default:
      super.onActivityResult(requestCode, resultCode, data);
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

#### Custom Credit Card Form

If you need to build your own credit card form, components inside `CreditCardActivity`
can be used on its own. For example, the `CreditCardEditText` can be used in XML in this way:

```xml
<co.omise.android.ui.CreditCardEditText
  android:layout_width="match_parent"
  android:layout_height="wrap_content" />
```

This component provides automatic spacing into groups of 4 digits as the user types.
Additionally the following utility classes are available from the SDK:

* `co.omise.android.ui.CreditCardEditText` - The `CreditCardEditText` class provides utility
   methods for validating and formatting credit card numbers.
* `co.omise.android.ui.CardNameEditText` - The `CardNameEditText` class handles formatting and
   input type for card holder name.
* `co.omise.android.ui.ExpiryDateEditText` - The `ExpiryDateEditText` class handles formatting and
  date range limitation.
* `co.omise.android.ui.SecurityCodeEditText` - The `SecurityCodeEditText` class handles formatting
   and input type for security code.

#### Manual Tokenization

If you have built your own credit card form you can use the SDK to manually tokenizes the
card. First build the `Client` and supply your public key like so:

```java
private val client = Client("pkey_test_123")
```

Then construct the token request with values from your custom form:

```java
val cardParam = CardParam(
                name = "JOHN Doe",
                number = "4242424242424242",
                expirationMonth = 10,
                expirationYear = 2020,
                securityCode = "123")

val request = Token.CreateTokenRequestBuilder(cardParam).build()
```

And then send the request using the `client` we've constructed earlier:

```java
client.send(request, object : RequestListener<Token>{
   override fun onRequestSucceed(model: Token) {
      // you've got Token!
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
Another way to use the Omise Android SDK is to integrate the `PaymentCreatorActivity` in order to allow users create a payment source from the list of
sources that are made available by for the implementer.

To use it, first declare the availability of the activity in your AndroidManifest.xml file as follows:

```xml
<activity
  android:name="co.omise.android.ui.PaymentCreatorActivity"
  android:theme="@style/OmiseTheme" />
```

Then in your activity, declare the method that will start this activity as follows:

```java
private val OMISE_PKEY : String = "pkey_test_123"
private val REQUEST_CC : Int = 100

private fun showCreditCardForm() {
val intent = Intent(this@CheckoutActivity, PaymentCreatorActivity::class.java)
intent.putExtra(OmiseActivity.EXTRA_PKEY, OMISE_PKEY)
intent.putExtra(OmiseActivity.EXTRA_AMOUNT, 1500.0)
intent.putExtra(OmiseActivity.EXTRA_CURRENCY, "thb")

// you can retrieve your account's capabilities through the SDk (will be explained in a different section)
intent.putExtra(OmiseActivity.EXTRA_CAPABILITY, capability)

startActivityForResult(intent, REQUEST_CC)
}
```

Replace the string pkey_test_123 with the public key obtained from your Omise dashboard.

After the end-user completes selecting and creating a payment source, the activity result callback will be called, handle it like so:

```java
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            // handle the cancellation
            return
        }

        if (requestCode == REQUEST_CC) {
            if (data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)) {
                Source source = data.getParcelableExtra(OmiseActivity.EXTRA_SOURCE_OBJECT);
                // process the source here
            } else if (data.hasExtra(OmiseActivity.EXTRA_TOKEN)) {
                Token token = data.getParcelableExtra(OmiseActivity.EXTRA_TOKEN_OBJECT);
                // process the token here
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
```

Two different results that could be returned are

* `data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)` - The `Source` object created by the payment creator.
* `data.hasExtra(OmiseActivity.EXTRA_TOKEN` - The `Token` object created in case the payment source created was a credit card.

### Creating a source
If you need to create a payment source on your own and use it outside of the provided SDK context, you can do follow these steps. First build the Client and supply your public key like so:

```java
private val client = Client("pkey_test_123")
```

Then construct the Source request

```java
val request = Source.CreateSourceRequestBuilder(250.0, "thb", SourceType.Installment.Bay)
      .description("Item")
      .email("e@mail.com")
      .storeId("id-123")
      .storeName("Store")
      .phoneNumber("06207658854")
      .installmentTerm(3)
      .build()

```

And then send the request using the `client` we've constructed earlier and you will get a Source in response:

```java
client.send(request, object : RequestListener<Source>{
   override fun onRequestSucceed(model: Source) {
      // you've got Source!
   }

    override fun onRequestFailed(throwable: Throwable) {
      // something bad happened
    }
})
```

### Retrieve Capabilities
You can retrieve all of your capabilities and available payment sources through the SDK in the following manner.

First build the Client and supply your public key like so:

```java
private val client = Client("pkey_test_123")
```

Then construct the Capability request

```java
val request = Capability.GetCapabilitiesRequestBuilder().build()
```

And then send the request using the client we've constructed earlier:

```java
client.send(request, object : RequestListener<Capability> {
   override fun onRequestSucceed(model: Capability) {
        // you have capabilities!
   }

   override fun onRequestFailed(throwable: Throwable) {
         // something bad happened
   }
   }
})
```

The Client class will automatically dispatch the network call on an internal background thread and will call listener methods on the thread that initially calls the send method.

## Authorizing Payment
Some payment method require the customers to authorize the payment via an authorized URL. This includes the [3-D Secure verification](https://www.omise.co/fraud-protection#3-d-secure), [Internet Banking payment](https://www.omise.co/offsite-payment), [Alipay](https://www.omise.co/alipay) and etc. Omise Android SDK provide a built in class to do the authorization.


#### Authorizing Payment activity

To use it, first declare the availability of the activity in your `AndroidManifest.xml`
file as follows:

```xml
<activity
  android:name="co.omise.android.ui.AuthorizingPaymentActivity"
  android:theme="@style/OmiseSDKTheme" />
```

Then in your activity, declare the method that will start this activity as follows:

```java
private void showAuthorizingPaymentForm() {
    Intent intent = new Intent(this, AuthorizingPaymentActivity.class);
    intent.putExtra(AuthorizingPaymentActivity.EXTRA_AUTHORIZED_URLSTRING, `AUTHORIZED_URL`);
    intent.putExtra(AuthorizingPaymentActivity.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, `EXPECTED_URL_PATTERNS` );
    startActivityForResult(intent, AUTHORIZING_PAYMENT_REQUEST_CODE);
}
```

Replace the string `AUTHORIZED_URL` with the authorized URL that comes with the created charge and the array of string `EXPECTED_URL_PATTERNS` with the expected pattern of redirected URLs array.

After the end-user completes the authorizing payment process, the activity result
callback will be called, handle it like so:

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == AUTHORIZING_PAYMENT_REQUEST_CODE && resultCode == RESULT_OK) {
        String url = data.getStringExtra(AuthorizingPaymentActivity.EXTRA_RETURNED_URLSTRING);
        // Use the redirected URL here.
    }
}
```

#### Authorizing Payment via an external app

Some request methods allow the user to authorize the payment with an external app, for example Alipay. When a user would like to authorize the payment with an external app, `AuthorizingPaymentActivity` will automatically open an external app by default. However merchant developers must handle the `Intent` callback by themselves.

## ProGuard Rules

If you enable ProGuard, then add this rules in your ProGuard file.

```ProGuard
-dontwarn okio.**
-dontwarn com.google.common.**
-dontwarn org.joda.time.**
-dontwarn javax.annotation.**
-dontwarn com.squareup.**
```


## Note on TLS 1.2
PCI-DSS standard requires the service to communicate in *TLS 1.2* or higher using strong encryption suite. This means that every clients must connect to Omise service with those valid suites. However TLS 1.2 support in Android is vary depends on the Android OS. Please follow the following instruction to add support for TLS 1.2 in your app.

#### Android API 20 or higher
You can use our SDK without any change. The SDK already fully supports communication to the Omise Service using TLS 1.2 with the properly encryption suite.

#### Android API 16 to API 19 with Google Play Services
These Android API versions don't support the proper encryption suites out of the box. However `Google Play Service` has the `ProviderInstaller` API to add support for the proper encryption suite. You may already use the Google Play Service in your app already; Google Play Service includes many common libraries used in many Android apps including GCM, Analytics and more. You can add support for the proper encryption suite with Google Play Service by doing the following steps

1. Add Google Play Service Analytics to your app grade setting

```gradle
 compile 'com.google.android.gms:play-services-analytics:16.0.3'
 ```
 
2. Asks the ProviderInstaller to install the encryption suites with the follow code

```java
 if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
     try {
         ProviderInstaller.installIfNeeded(this);
     } catch (GooglePlayServicesRepairableException e) {
         e.printStackTrace();
     } catch (GooglePlayServicesNotAvailableException e) {
         e.printStackTrace();
     }
 }
 ```
 
> **Note:** Google Play Service may not available on every brands or models. Please be concerned about the Google Play Service compatibility

#### API 15 or lower
These Android API levels could not support for the proper encryption suites easily which means that the Android devices running those OS versions may not be able to use to connect to many services on the internet. We recommend you to drop support for those API levels


## Contributing

Pull requests and bugfixes are welcome. For larger scope of work, please pop on to our [forum](https://forum.omise.co) to discuss first.


## LICENSE

MIT [See the full license text](https://github.com/omise/omise-android/blob/master/LICENSE)
