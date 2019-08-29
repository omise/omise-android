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
        @JsonValue val name: String?
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
    object InstBankingBay : SourceType("installment_bay")
    object InstFirstChoice : SourceType("installment_first_choice")
    object InstBbl : SourceType("installment_bbl")
    object InstKtc : SourceType("installment_ktc")
    object InstKBank : SourceType("installment_kbank")
    object Unknown : SourceType(null)

    companion object {
        @SuppressLint("DefaultLocale")
        @JsonCreator
        @JvmStatic
        fun creator(name: String?): SourceType? {
            return SourceType::class.sealedSubclasses.firstOrNull {
                it.simpleName?.toLowerCase() == name?.toLowerCase()
            }?.objectInstance
        }
    }
}

object SourceTypeParceler : Parceler<SourceType> {
    override fun create(parcel: Parcel): SourceType {
        val sourceType = SourceType.creator(parcel.readString())
        return sourceType ?: SourceType.Unknown
    }

    override fun SourceType.write(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }
}