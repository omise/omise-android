# Omise Android SDK

[![](https://img.shields.io/maven-central/v/co.omise/omise-android.svg?style=flat-square)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22co.omise%22%20AND%20a%3A%22omise-android%22)
[![](https://img.shields.io/gitter/room/omise/omise-android.svg?style=flat-square)](https://gitter.im/omise/omise-android)
[![](https://img.shields.io/badge/email-support-yellow.svg?style=flat-square)](mailto:support@omise.co)

Omise is a payment service provider currently operating in Thailand. Omise provides a set
of clean APIs that helps merchants of any size accept credit cards online.

Omise Android SDK provides Android bindings for the Omise
[Tokenization](https://www.omise.co/tokens-api) API so you do not need to pass credit card
data to your server as well as components for entering credit card information.

Hop into the Gitter chat (click the badge above) or email our support team if you have any
question regarding this SDK and the functionality it provides.

## Requirements

* Public key. [Register for an Omise account](https://dashboard.omise.co/signup) to obtain your API keys.
* Android 4.4+ (KitKat) target or higher.
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
compile 'co.omise:omise-android:2.0.+'
```

## Usage

#### Credit Card Activity

The simplest way to use this SDK is to integrate the provided `CreditCardActivity`
directly into your application. This activity contains a pre-made credit form and will
automatically [tokenize credit card
information](https://www.omise.co/security-best-practices) for you.

To use it, first declare the availability of the activity in your `AndroidManifest.xml`
file as follows:

```xml
<activity
  android:name="co.omise.android.ui.CreditCardActivity"
  android:theme="@style/OmiseSDKTheme" />
```

Then in your activity, declare the method that will start this activity as follows:

```java
private static final String OMISE_PKEY = "pkey_test_123”;
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
      if (resultCode == CreditCardActivity.RESULT_CANCEL) {
        return;
      }

      Token token = data.getParcelableExtra(CreditCardActivity.EXTRA_TOKEN_OBJECT);
      // process your token here.

    default:
      super.onActivityResult(requestCode, resultCode, data);
  }
}
```

A number of results are returned as from the activity. You can obtain them from the
resulting `Intent` with the following code:

* `data.getStringExtra(CreditCardActivity.EXTRA_TOKEN)` - The string ID of the token. Use
  this if you only needs the ID and not   the card data.
* `data.getParcelableExtra(CreditCardActivity.EXTRA_TOKEN_OBJECT)` - The full `Token`
  object returned from the Omise API.
* `data.getParcelableExtra(CreditCardActivity.EXTRA_CARD_OBJECT)` - The `Card` object
  which is part of the `Token` object returned from the Omise API.

#### Custom Credit Card Form

If you need to build your own credit card form, components inside `CreditCardActivity`
can be used on its own. For example, the `CreditCardEditText` can be used in XML like so:

```xml
<co.omise.android.ui.CreditCardEditText
  android:layout_width="match_parent"
  android:layout_height="wrap_content" />
```

This component provides automatic spacing into groups of 4 digits as the user types.
Additionally the following utility classes are available from the SDK:

* `co.omise.android.ui.ExpiryMonthSpinnerAdapter` - This is a
  [SpinnerAdapter](https://developer.android.com/reference/android/widget/SpinnerAdapter.html)
  that provide list of months (01-12) for use in a
  [Spinner](https://developer.android.com/guide/topics/ui/controls/spinner.html) control
  for selecting expiry dates.
* `co.omise.android.ui.ExpiryYearSpinnerAdapter` - Same as above but lists the current
  year up to twelve years into the future.
* `co.omise.android.CardNumber` - The `CardNumber` class provides utility methods for
  validating and formatting credit card numbers.

#### Manual Tokenization

If you have built your own credit card form you can use the SDK to manually tokenizes the
card. First build the `Client` and supply your public key like so:

```java
Client client = new Client("pkey_test_123");
```

Then construct the token request with values from your custom form:

```java
TokenRequest request = new TokenRequest();
request.number = "4242424242424242";
request.name = "JOHN SMITH";
request.expirationMonth = 10;
request.expirationYear = 2020;
request.securityCode = "123";
```

And then send the request using the `client` we've constructed earlier:

```java
client.send(request, new TokenRequestListener() {
  @Override
  public void onTokenRequestSucceed(TokenRequest request, Token token) {
      // you've got Token!
  }

  @Override
  public void onTokenRequestFailed(TokenRequest request, Throwable throwable) {
      // something bad happened
  }
});
```

The `Client` class will automatically dispatch the network call on an internal background
thread and will call listener methods on the thread that initially calls the `send`
method.

## Card.io support

This library supports integration with
[Card.IO Android SDK](https://github.com/card-io/card.io-Android-SDK) which enables credit
card scanning with phone camera. To enable this integration, simply include the library
with your project and the SDK will pick it up automatically. A camera button will be added
when `CreditCardActivity` is shown.

Check [Card.io SDK setup](https://github.com/card-io/card.io-Android-SDK#setup) as the
canonical source of information. For convenience, the steps are summarized here:

1. Adds `compile 'io.card:android-sdk:5.4.0'` to your `build.gradle` dependencies list.
2. Adds the following `uses-permission` and `uses-feature` to your `AndroidManifest.xml`
   file:
   ```xml
   <uses-permission android:name="android.permission.VIBRATE" />
   <uses-permission android:name="android.permission.CAMERA" />

   <uses-feature android:name="android.hardware.camera" android:required="false" />
   <uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />
   <uses-feature android:name="android.hardware.camera.flash" android:required="false" />
   ```
3. In the same file, adds Card.io Activity declarations:
   ```xml
   <activity android:name="io.card.payment.CardIOActivity" />
   ```

That's it! The SDK should now picks up card.io and shows a camera button automatically.

## Contributing

Pull requests and bugfixes are welcome. For larger scope of work, please pop on to our
[![](https://img.shields.io/gitter/room/omise/omise-android.svg?style=flat-square)](https://gitter.im/omise/omise-android)
chatroom to discuss first.

## LICENSE

MIT (See the (full license text)[https://github.com/omise/omise-android/blob/master/LICENSE])
