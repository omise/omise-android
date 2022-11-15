# CHANGE LOG

## v4.4.0
* **ADDED:** Installment Maybank for Malaysian merchants
* **REMOVED:** Unused and deprecated Installment Ezypay

## v4.3.1
* **FIXED:** Fix JCenter deprecation
* **CHANGED:** Switch authorization to use only in-app browser

## v4.3.0
* **ADDED:** Added support ShopeePay JumpApp.

## v4.2.0
* **ADDED:** Added support for Boost, DuitNow QR, DuitNow Online Banking/Wallets, Maybank QRPay and ShopeePay payment method.
* **CHANGED** Update Touch 'n Go and Grabpay to support RMS provider.

## v4.1.4
* **ADDED:** Added Bank of China logo for FPX payments.
* **CHANGED** Fixed allowed installment terms for UOB and TTB installments.

## v4.1.3
* **CHANGED:** Added support for GrabPay payment method.

## v4.1.2
* **CHANGED:** Move OCBC Pay Anyone payment out of Mobile Banking section.

## v4.1.1
* **CHANGED** 3DS library version to 1.0.0-alpha09

## v4.1.0
* **ADDED:** Support for GooglePay
* **ADDED:** Support for BBL mobile banking 
* **CHANGED:** Logo and label for mobile banking payments: Kbank (TH), Bay (TH) and SCB (TH)

## v4.0.0

* **CHANGED:** Require minimum SDK version 21
* **ADDED:** 3D Secure version 2
* **ADDED:** Observing `ChargeStatus` change with Token ID
* **ADDED:** `platform_type` field for Source
* **ADDED:** Support for wallets: Alipay (SG), AlipayHK (SG), DANA (SG), GCash (SG), Kakao Pay (SG), Touch'n Go (SG) and Rabbit LINE Pay (TH)
* **ADDED:** Support for installments: Citi (TH), TTB (TH), UOB (TH) and Ezypay (MY)
* **ADDED:** Support for mobile banking payments: Kbank (TH), Bay (TH) and OCBC Pay Anyone (SG)

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
