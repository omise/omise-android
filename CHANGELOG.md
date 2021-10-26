# CHANGE LOG


## v4.0.0-alpha04

* **ADDED:** Support for Citi installment
* **ADDED:** Support for TTB installment
* **ADDED:** Support for Uob installment

## v4.0.0-alpha03

* **ADDED:** Support for Alipay, AlipayHK, DANA, GCash, Kakao Pay, and Touch'n Go
* **ADDED:** `platform_type` field for Source

## v4.0.0-alpha02

* **ADDED:** EzyPay installment payment to Android SDK


## v4.0.0-alpha01

* **CHANGED:** Require minimum SDK version 21
* **ADDED:** 3D Secure version 2
* **ADDED:** Observing `ChargeStatus` change with Token ID

## v3.2.1

* **FIXED:** Fix can not open WebView on Android 5.0 (#178)

## v3.2.0

* **CHANGED:** Support Android API 30
* **CHANGED:** Support displaying dialog from JavaScript alert, confirm, prompt functions

## v3.1.3

* **ADDED:** Support SCB installment
* **ADDED:** Support SCB mobile banking
* **CHANGED:** Drop CircleCI and migrate to Github Actions

## v3.1.2

* **CHANGED:** Enhance `AuthorizingPaymentActivity` to support cache in the WebView for short life time

## v3.1.1

* **FIXED:** Fix a potential security on the `jackson-core` lib, issue #136

## v3.1.0

* **ADDED** Add `mobileNumber`, `scannableCode`, `zeroInterestInstallments` fields to Source model
* **ADDED:** Support new source types (PointsCiti, PromptPay, PayNow)
* **ADDED:** Support new payment methods in the `PaymentCreatorActivity` (PointsCiti, PromptPay, PayNow, TrueMoney)
* **ADDED:** Support the term zero-interest installments in the `PaymentCreatorActivity`
* **CHANGED:** Update payment method icons

## v3.0.0

* **CHANGED:** API request process
* **ADDED:** PaymentCreatorActivity for creating different payment sources
* **ADDED:** Ability to retrieve account Capability
* **ADDED:** Ability to create Source
* **ADDED:** Theme Customization
* **REMOVED:** Laser card support
* **CHANGED:** Credit Card form design
* **CHANGED:** Converted most of the SDK from Java to Kotlin

## v2.6.6

* **FIXED:** Fix expiry date spinner.

## v2.6.5

* **REMOVED:** Drop support TLS 1.1.
* **ADDED:** Add pin certification.
* **CHANGED:** Change result callback code to use default RESULT_OK/RESULT_CANCELED from Activity in CreditCardActivity. 

## v2.6.4

* **NEW:** Allow the user to authorize the payment with an external app.

## v2.6.3

* **NEW:** Add `bank` field to Card.
