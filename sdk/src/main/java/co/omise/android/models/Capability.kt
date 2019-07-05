package co.omise.android.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

class Capability : Model {
    private var banks: List<String>? = null
    private var paymentMethods: List<PaymentMethod>? = null
    private var zeroInterestInstallments: Boolean = false


    constructor(rawJson: String) : this(JSONObject(rawJson))

    private constructor(json: JSONObject) : super(json) {
        banks = JSON.stringList(json, "banks")
        paymentMethods = JSON.modelList(json, "payment_methods", PaymentMethod::class.java)
        zeroInterestInstallments = JSON.bool(json, "zero_interest_installments")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeStringList(banks)
        parcel.writeTypedList(paymentMethods)
        parcel.writeByte(if (zeroInterestInstallments) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Capability> {
        override fun createFromParcel(parcel: Parcel): Capability {
            return Capability(parcel.readString())
        }

        override fun newArray(size: Int): Array<Capability?> {
            return arrayOfNulls(size)
        }
    }

}