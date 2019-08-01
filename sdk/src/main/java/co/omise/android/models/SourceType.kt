package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonCreator

sealed class SourceType(
        val name: String?
) : Model() {

    object InternetBankingBay : SourceType("internet_banking_bay")
    object InternetBankingKtb : SourceType("internet_banking_ktb")
    object InternetBankingScb : SourceType("internet_banking_scb")
    object InternetBankingBbl : SourceType("internet_banking_bbl")
    object Alipay : SourceType("alipay")
    object BillPaymentTescoLotus : SourceType("bill_payment_tesco_lotus")
    object BarcodeAlipay : SourceType("barcode_alipay")
    object Econtext : SourceType("econtext")
    object TrueMoney : SourceType("truemoney")
    object InstBankingBay : SourceType("installment_bay")
    object InstFirstChoice : SourceType("installment_first_choice")
    object InstBbl : SourceType("installment_bbl")
    object InstKtc : SourceType("installment_ktc")
    object InstKBank : SourceType("installment_kbank")
    object Unknown : SourceType(null)

    companion object {
        @JsonCreator
        @JvmStatic
        private fun creator(name: String): SourceType? {
            return SourceType::class.sealedSubclasses.find {
                it.simpleName == name
            }?.objectInstance
        }
    }
}
