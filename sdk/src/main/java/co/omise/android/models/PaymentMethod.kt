package co.omise.android.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentMethod(
        var name: String? = null,
        var currencies: List<String>? = null,
        @field:JsonProperty("card_brands")
        var cardBrands: List<String>? = null,
        @field:JsonProperty("installment_terms")
        var installmentTerms: List<Int>? = null
) : Model(), Parcelable {

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        currencies = parcel.createStringArrayList()
        cardBrands = parcel.createStringArrayList()
        installmentTerms = parcel.createIntArray()?.asList()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeStringArray(currencies?.toTypedArray())
        dest.writeStringArray(cardBrands?.toTypedArray())
        dest.writeIntArray(installmentTerms?.toIntArray())
    }

    companion object CREATOR : Parcelable.Creator<PaymentMethod> {
        override fun createFromParcel(parcel: Parcel): PaymentMethod {
            return PaymentMethod(parcel)
        }

        override fun newArray(size: Int): Array<PaymentMethod?> {
            return arrayOfNulls(size)
        }
    }
}
