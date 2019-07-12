package co.omise.android.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty

data class Card(
        @JvmField
        var country: String? = null,
        @JvmField
        var city: String? = null,
        @JvmField
        @field:JsonProperty("postal_code")
        var postalCode: String? = null,
        @JvmField
        var financing: String? = null,
        @JvmField
        @field:JsonProperty("last_digits")
        var lastDigits: String? = null,
        @JvmField
        var brand: String? = null,
        @JvmField
        @field:JsonProperty("expiration_month")
        var expirationMonth: Int = 0,
        @JvmField
        @field:JsonProperty("expiration_year")
        var expirationYear: Int = 0,
        @JvmField
        var fingerprint: String? = null,
        @JvmField
        var name: String? = null,
        @JvmField
        @field:JsonProperty("security_code_check")
        var securityCodeCheck: Boolean = false,
        @JvmField
        var bank: String? = null
) : Model(), Parcelable {

    constructor(parcel: Parcel) : this() {
        country = parcel.readString()
        city = parcel.readString()
        postalCode = parcel.readString()
        financing = parcel.readString()
        lastDigits = parcel.readString()
        brand = parcel.readString()
        expirationMonth = parcel.readInt()
        expirationYear = parcel.readInt()
        fingerprint = parcel.readString()
        name = parcel.readString()
        securityCodeCheck = parcel.readInt() == 1
        bank = parcel.readString()
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(country)
        dest.writeString(city)
        dest.writeString(postalCode)
        dest.writeString(financing)
        dest.writeString(lastDigits)
        dest.writeString(brand)
        dest.writeInt(expirationMonth)
        dest.writeInt(expirationYear)
        dest.writeString(fingerprint)
        dest.writeString(name)
        dest.writeInt(if (securityCodeCheck) 1 else 0)
        dest.writeString(bank)
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
