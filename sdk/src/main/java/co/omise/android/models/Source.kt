package co.omise.android.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty

data class Source(
        var type: SourceType? = null,
        var flow: FlowType? = null,
        var amount: Long = 0,
        var currency: String? = null,
        var barcode: String? = null,
        var references: References? = null,
        @JsonProperty("store_id")
        var storeId: String? = null,
        @JsonProperty("store_name")
        var storeName: String? = null,
        @JsonProperty("terminal_id")
        var terminalId: String? = null,
        var name: String? = null,
        var email: String? = null,
        @JsonProperty("phone_number")
        var phoneNumber: String? = null,
        @JsonProperty("installment_term")
        var installmentTerm: Int = 0
) : Model(), Parcelable {

    constructor(parcel: Parcel) : this() {
        type = parcel.readParcelable(SourceType::class.java.classLoader)
        flow = parcel.readParcelable(FlowType::class.java.classLoader)
        amount = parcel.readLong()
        currency = parcel.readString()
        barcode = parcel.readString()
        references = parcel.readParcelable(References::class.java.classLoader)
        storeId = parcel.readString()
        storeName = parcel.readString()
        terminalId = parcel.readString()
        name = parcel.readString()
        email = parcel.readString()
        phoneNumber = parcel.readString()
        installmentTerm = parcel.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(type, flags)
        dest.writeParcelable(flow, flags)
        dest.writeLong(amount)
        dest.writeString(currency)
        dest.writeString(barcode)
        dest.writeParcelable(references, flags)
        dest.writeString(storeId)
        dest.writeString(storeName)
        dest.writeString(terminalId)
        dest.writeString(name)
        dest.writeString(email)
        dest.writeString(phoneNumber)
        dest.writeInt(installmentTerm)
    }

    companion object CREATOR : Parcelable.Creator<Source> {
        override fun createFromParcel(parcel: Parcel): Source {
            return Source(parcel)
        }

        override fun newArray(size: Int): Array<Source?> {
            return arrayOfNulls(size)
        }
    }
}
