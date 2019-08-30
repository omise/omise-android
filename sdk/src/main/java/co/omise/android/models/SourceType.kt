package co.omise.android.models

import android.annotation.SuppressLint
import android.os.Parcel
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.android.parcel.Parceler

/**
 * Represents Source Type object.
 *
 * @see [Sources API](https://www.omise.co/sources-api)
 */
sealed class SourceType(
        @JsonValue open val name: String?
) {

    object InternetBankingBay : SourceType("internet_banking_bay")
    object InternetBankingKtb : SourceType("internet_banking_ktb")
    object InternetBankingScb : SourceType("internet_banking_scb")
    object InternetBankingBbl : SourceType("internet_banking_bbl")
    object Alipay : SourceType("alipay")
    object BillPaymentTescoLotus : SourceType("bill_payment_tesco_lotus")
    object BarcodeAlipay : SourceType("barcode_alipay")
    object Econtext : SourceType("econtext")
    object TrueMoney : SourceType("truemoney")
    object InstallmentBay : SourceType("installment_bay")
    object InstallmentFirstChoice : SourceType("installment_first_choice")
    object InstallmentBbl : SourceType("installment_bbl")
    object InstallmentKtc : SourceType("installment_ktc")
    object InstallmentKBank : SourceType("installment_kbank")
    data class Unknown(override val name: String?) : SourceType(name)

    companion object {
        @SuppressLint("DefaultLocale")
        @JsonCreator
        @JvmStatic
        fun creator(name: String?): SourceType = when (name) {
            "internet_banking_bay" -> InternetBankingBay
            "internet_banking_ktb" -> InternetBankingKtb
            "internet_banking_scb" -> InternetBankingScb
            "internet_banking_bbl" -> InternetBankingBbl
            "alipay" -> Alipay
            "bill_payment_tesco_lotus" -> BillPaymentTescoLotus
            "barcode_alipay" -> BarcodeAlipay
            "econtext" -> Econtext
            "truemoney" -> TrueMoney
            "installment_bay" -> InstallmentBay
            "installment_first_choice" -> InstallmentFirstChoice
            "installment_bbl" -> InstallmentBbl
            "installment_ktc" -> InstallmentKtc
            "installment_kbank" -> InstallmentKBank
            else -> Unknown(name)
        }
    }
}

object SourceTypeParceler : Parceler<SourceType> {
    override fun create(parcel: Parcel): SourceType {
        return SourceType.creator(parcel.readString())
    }

    override fun SourceType.write(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }
}
