# 3D Secure V2

## Get Started

N/A

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
