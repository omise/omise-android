# CHANGE LOG

## v3.1.1

* **FIXED:** Fix a potential security on the `jackson-core` lib, issue #136

## v3.1.0

* **ADDED** Add `mobileNumber`, `scannableCode`, `zeroInterestInstallments` fields to Source model
* **ADDED:** Support new source types (PointsCiti, PromptPay, PayNow)
* **ADDED:** Support new payment methods in the `PaymentCreatorActivity` (PointsCiti, PromptPay, PayNow, TrueMoney)
* **ADDED:** Support the term zero-interest installments in the `PaymentCreatorActivity`
* **CHANGED** Update payment method icons

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