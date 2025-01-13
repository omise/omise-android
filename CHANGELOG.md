# CHANGELOG

## V5.5.0
* **ADDED:** `installment_wlb_uob` source support.

## V5.4.0
* **Fix:** Include missing classes in proguard when pro guard is enabled to prevent SDK crashing in particular scenarios.

## V5.3.0
* **Fix:** Do not auto close browser when payment app does not exist and deeplink fails to open.

## V5.2.0
* **Enhancement:** Force load capabilities from api and remove hard coded values.

## V5.1.0
* **ADDED:** Implemented new sources `installment_wlb_bay`, `installment_wlb_bbl`, `installment_wlb_first_choice`, `installment_wlb_kbank`, `installment_wlb_scb`, `installment_wlb_ttb`
* **FIX:** Included missing 3DS sdk into Omise sdk**

## V5.0.0
### Breaking Changes
* **UI Customization Overhaul**: Introducing a new theme-based UI customization approach. Previously customized UI configurations must be updated. This offers configurations for `DefaultTheme`, `DarkTheme`, and `MonoChromeTheme`. See `CheckoutActivity.kt` in the example app for detailed implementation.
* **Charge Authorization**: A new parameter named `EXTRA_THREE_DS_REQUESTOR_APP_URL` has been introduced and is required for charge authorization. Ensure this parameter is passed during the start of `AuthorizingPaymentActivity` to prevent errors.
* **Automatic Initialization**: The function `initializeAuthoringPaymentConfig()` has been removed and is no longer needed. The SDK now handles initialization automatically.
### Enhancements
* **Native 3DS Charge Authorization:** Provides more up-to-date security measures and improves the user experience. Also adds a protection layer against unauthorized card usage, reducing merchants' and customers' fraud risk.
* **WebView Closed Callback:**  Added a new callback code named `WEBVIEW_CLOSED_RESULT_CODE` to inform integrators if the charge authorization is returning from a webView.
### Upgrades
* **Gradle**: upgrade gradle to 8.2 and AGP to 8.2.2
* **SDK versions:** Set target and compile sdk versions to 34 and min sdk version to 21

## V4.17.1
* **ADDED:** Implemented new source `installment_wlb_ktc`

## V4.16.1

- **REMOVED:** Remove obsolete `points_citi`
- **ADDED:** Added installment terms calculations

## V4.15.1

- **PATCH:** Fix error message for `installment_mbb`

## V4.15.0

- **REMOVED:** Remove obsolete `ocbc_pao` and `installment_citi`

## V4.14.0

- **ADDED:** Support new source type `wechat_pay`

## V4.13.1

- **ADDED:** Add kotlin linting standards using ktlint-gradle library
- **CHANGED:** Removed nullability from text field callbacks
- **PATCH:** Fix api call for Atome source

## V4.13.0

- **ADDED:** Support new source type `truemoney_jumpapp`

## V4.12.0

- **ADDED:** Prevent screenshot and screen recording in `AuthorizingPaymentActivity`, `PaymentCreatorActivity` and `CreditCardActivity` as default behavior
- **FIXED:** Fix app crash when non numeric characters characters are entered in expiry date field for credit cards
- **CHANGED:** Add new logo for OCBC payment method

## v4.11.0

- **ADDED:** Supported OCBC Digital payment method
- **ADDED:** Supported authorize payment through the external app in the `AuthorizingPaymentActivity`
- **ADDED:** Put coverage report to SonarCloud

## v4.10.0

- **ADDED:** Supported billing address for credit card form

## v4.9.0

- **CHANGED:** Updated Android Gradle plugin to version 7.4.2
- **CHANGED:** Updated Kotlin to version 1.7.10
- **CHANGED:** Updated 3DS SDK to version 1.0.0-alpha12

## v4.8.0

- **REMOVED:** Removed support for SCB and KTB Internet Banking payment method

## v4.7.0

- **ADDED:** Added support for PayPay payment method

## v4.6.0

- **ADDED:** Added support for Krungthai NEXT (mobile_banking_ktb) payment method

## v4.5.2

- **CHANGED:** Update logo and name of Lotus's Bill Payment

## v4.5.1

- **FIXED:** Patch explicit error thrown when redirecting to non-existing custom scheme uri

## v4.5.0

- **ADDED:** Added support for Atome payment method

## v4.4.2

- **CHANGED:** Logo and label for Lotus Bill Payment

## v4.4.1

- **FIXED:** Patch 3DS SDK

## v4.4.0

- **ADDED:** Installment Maybank for Malaysian merchants
- **REMOVED:** Unused and deprecated Installment Ezypay

## v4.3.1

- **FIXED:** Fix JCenter deprecation
- **CHANGED:** Switch authorization to use only in-app browser

## v4.3.0

- **ADDED:** Added support ShopeePay JumpApp.

## v4.2.0

- **ADDED:** Added support for Boost, DuitNow QR, DuitNow Online Banking/Wallets, Maybank QRPay and ShopeePay payment method.
- **CHANGED** Update Touch 'n Go and Grabpay to support RMS provider.

## v4.1.4

- **ADDED:** Added Bank of China logo for FPX payments.
- **CHANGED** Fixed allowed installment terms for UOB and TTB installments.

## v4.1.3

- **CHANGED:** Added support for GrabPay payment method.

## v4.1.2

- **CHANGED:** Move OCBC Pay Anyone payment out of Mobile Banking section.

## v4.1.1

- **CHANGED** 3DS library version to 1.0.0-alpha09

## v4.1.0

- **ADDED:** Support for GooglePay
- **ADDED:** Support for BBL mobile banking
- **CHANGED:** Logo and label for mobile banking payments: Kbank (TH), Bay (TH) and SCB (TH)

## v4.0.0

- **CHANGED:** Require minimum SDK version 21
- **ADDED:** 3D Secure version 2
- **ADDED:** Observing `ChargeStatus` change with Token ID
- **ADDED:** `platform_type` field for Source
- **ADDED:** Support for wallets: Alipay (SG), AlipayHK (SG), DANA (SG), GCash (SG), Kakao Pay (SG), Touch'n Go (SG) and Rabbit LINE Pay (TH)
- **ADDED:** Support for installments: Citi (TH), TTB (TH), UOB (TH) and Ezypay (MY)
- **ADDED:** Support for mobile banking payments: Kbank (TH), Bay (TH) and OCBC Pay Anyone (SG)

## v3.2.1

- **FIXED:** Fix can not open WebView on Android 5.0 (#178)

## v3.2.0

- **CHANGED:** Support Android API 30
- **CHANGED:** Support displaying dialog from JavaScript alert, confirm, prompt functions

## v3.1.3

- **ADDED:** Support SCB installment
- **ADDED:** Support SCB mobile banking
- **CHANGED:** Drop CircleCI and migrate to Github Actions

## v3.1.2

- **CHANGED:** Enhance `AuthorizingPaymentActivity` to support cache in the WebView for short life time

## v3.1.1

- **FIXED:** Fix a potential security on the `jackson-core` lib, issue #136

## v3.1.0

- **ADDED** Add `mobileNumber`, `scannableCode`, `zeroInterestInstallments` fields to Source model
- **ADDED:** Support new source types (PointsCiti, PromptPay, PayNow)
- **ADDED:** Support new payment methods in the `PaymentCreatorActivity` (PointsCiti, PromptPay, PayNow, TrueMoney)
- **ADDED:** Support the term zero-interest installments in the `PaymentCreatorActivity`
- **CHANGED:** Update payment method icons

## v3.0.0

- **CHANGED:** API request process
- **ADDED:** PaymentCreatorActivity for creating different payment sources
- **ADDED:** Ability to retrieve account Capability
- **ADDED:** Ability to create Source
- **ADDED:** Theme Customization
- **REMOVED:** Laser card support
- **CHANGED:** Credit Card form design
- **CHANGED:** Converted most of the SDK from Java to Kotlin

## v2.6.6

- **FIXED:** Fix expiry date spinner.

## v2.6.5

- **REMOVED:** Drop support TLS 1.1.
- **ADDED:** Add pin certification.
- **CHANGED:** Change result callback code to use default RESULT_OK/RESULT_CANCELED from Activity in CreditCardActivity.

## v2.6.4

- **NEW:** Allow the user to authorize the payment with an external app.

## v2.6.3

- **NEW:** Add `bank` field to Card.
