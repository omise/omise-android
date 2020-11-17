# 3D Secure V2

Unlike previous version the cardholder is redirected to the bank website, the 3D Secure V2 provided stronge customer authentication and the customer can authenticate within the merchant application.

## Get Started

To use the 3D Secure v2 you need to execute the `AuthorizingPaymentConfig.initialize(config)` function before starting the authorizing payment with the `authorize_uri` [see more](https://www.omise.co/how-to-implement-3-D-Secure).

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

These are configuration allowed in the 3DS v2.

| Config | Description |
|---|---|
| `uiCustomization` | Configuration for UI customization in the challenge flow. |
| `timeout` | Maximum timeout for the challenge flow. The acceptable timeout is 5-99 mins. |

To use the authentication page, the Omise SDK provides the `AuthorizingPaymentActivity` for handle the authorizing payment. You can declare the `AuthorizingPaymentActivity` in your `AndroidManifest.xml` file.

```xml
<activity
  android:name="co.omise.android.ui.AuthorizingPaymentActivity"
  android:theme="@style/OmiseTheme" />
```

Then in your activity, you can use the `startActivityForResult()` function to start the `AuthorizingPaymentActivity` and handle the authentication result.

```kotlin
val intent = Intent(this, AuthorizingPaymentActivity::class.java)
intent.putExtra(AuthorizingPaymentURLVerifier.EXTRA_AUTHORIZED_URLSTRING, AUTHORIZED_URL)
intent.putExtra(AuthorizingPaymentURLVerifier.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, EXPECTED_URL_PATTERNS)
intent.putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY)
intent.putExtra(OmiseActivity.EXTRA_TOKEN, TOKEN_ID)
startActivityForResult(intent, AUTHORIZING_PAYMENT_REQUEST_CODE)
```

After the end-user had completed the authorizing payment process,  the `onActivityResult()` function will be called, you can handle the authorizing payment result in there.

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == AUTHORIZING_PAYMENT_REQUEST_CODE && resultCode == RESULT_OK) {
        // Available only in 3D Secure V1
        val url = data?.getStringExtra(AuthorizingPaymentURLVerifier.EXTRA_RETURNED_URLSTRING)
        // Available only in 3D Secure V2
        val token = data?.getParcelableExtra<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
    }
}
```


You can checkout the sample implementation in the [PaymentProcessingActivity](sample-threeds-v2-app/src/main/java/co/omise/android/example/PaymentProcessingActivity.kt) class in the **sample-threeds-v2-app** app. 
## Using UI customization

In the challenge flow, the Omise SDK allows developers to customize UI elements in the challenge flow. To customize UI, you can create an instance of `UiCustomization` and set your preferred properties through the `UiCustomization` class. Finally, set `UiCustomization` instance through `ThreeDSConfig.uiCustomization()` function.

```kotlin
val uiCustomization = UiCustomization.Builder()
          .labelCustomization(LabelCustomization.Builder()
                  .textFontName("RobotoMono-Regular.ttf")
                  .textFontColor("#000000")
                  .textFontSize(16)
                  .build())
  val threeDSConfig = ThreeDSConfig.Builder()
          .uiCustomization(uiCustomization)
          .build()
```

You can checkout the [`UiCustomization`](/sdk/src/main/java/co/omise/android/config/UiCustomization.kt) class to see customizable elements that you can customize in the challenge flow.

