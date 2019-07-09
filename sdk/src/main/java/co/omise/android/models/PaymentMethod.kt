package co.omise.android.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

class PaymentMethod : Model {
    var name: String? = null
    var currencies: List<String>? = null
    var cardBrands: List<String>? = null
    var installmentTerms: List<Int>? = null


    constructor(rawJson: String) : this(JSONObject(rawJson))

    private constructor(json: JSONObject) : super(json) {
        name = JSON.string(json, "name")
        currencies = JSON.stringList(json, "currencies")
        cardBrands = JSON.stringList(json, "card_brands")
        installmentTerms = JSON.integerList(json, "installment_terms")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(name)
        parcel.writeStringList(currencies)
        parcel.writeStringList(cardBrands)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentMethod> {
        override fun createFromParcel(parcel: Parcel): PaymentMethod {
            return PaymentMethod(parcel.readString())
        }

        override fun newArray(size: Int): Array<PaymentMethod?> {
            return arrayOfNulls(size)
        }
    }
}