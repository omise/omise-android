package co.omise.android.models

sealed class SourceType(val name: String?) {
    class InternetBankingBay : SourceType("internet_banking_bay")
    class InternetBankingKtb : SourceType("internet_banking_ktb")
    class InternetBankingScb : SourceType("internet_banking_scb")
    class InternetBankingBbl : SourceType("internet_banking_bbl")
    class Alipay : SourceType("alipay")
    class BillPaymentTescoLotus : SourceType("bill_payment_tesco_lotus")
    class BarcodeAlipay : SourceType("barcode_alipay")
    class Econtext : SourceType("econtext")
    class TrueMoney : SourceType("truemoney")
    class InstBankingBay : SourceType("installment_bay")
    class InstFirstChoice : SourceType("installment_first_choice")
    class InstBbl : SourceType("installment_bbl")
    class InstKtc : SourceType("installment_ktc")
    class InstKBank : SourceType("installment_kbank")
    class Unknown : SourceType(null)
}