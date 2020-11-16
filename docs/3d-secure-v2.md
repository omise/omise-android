# 3D Secure V2

## Get Started


### Default UI


### Using UI customization

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

These are customizable elements that you can customize them.

| Element | Property |
|---|---|
| Header | Text color<br/>Text size<br/>Font |
| Text | Text color<br/>Text size<br/>Font |
| Text box | Text color<br/>Text size<br/>Font<br/>Border color<br/>Border width<br/>Corner radius |
| Button | Text color<br/>Text size<br/>Font<br/>Border width<br/>Background color |
| Toolbar | Title text<br/>Cancel button text<br/>Background color<br/>Text size<br/>Text color<br/>Font |

- font use font from assets directory
- color use hex color

- sample

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

```kotlin
val uiCustomization = UiCustomization.Builder()
          .labelCustomization(LabelCustomization.Builder()
                  .textFontName("RobotoMono-Regular.ttf")
                  .textFontColor("#000000")
                  .textFontSize(16)
                  .build())
  val threeDSConfig = ThreeDSConfig.Builder()
          .uiCustomization(uiCustomization)
          .timeout(5)
          .build()
  val authPaymentConfig = AuthorizingPaymentConfig.Builder()
          .threeDSConfig(threeDSConfig)
          .build()
  AuthorizingPaymentConfig.initialize(authPaymentConfig)
```