package co.omise.android.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import org.joda.time.DateTime

/**
 * Represents Source References object.
 *
 * @see [Sources API](https://www.omise.co/sources-api)
 */
data class References(
        @field:JsonProperty("va_code")
        var vaCode: String? = null,
        @field:JsonProperty("omise_tax_id")
        var omiseTaxId: String? = null,
        @field:JsonProperty("reference_number_1")
        var referenceNumber1: String? = null,
        @field:JsonProperty("reference_number_2")
        var referenceNumber2: String? = null,
        var barcode: String? = null,
        @field:JsonProperty("expires_at")
        var expiresAt: DateTime? = null
) : Model() {

    constructor(parcel: Parcel) : this() {
        vaCode = parcel.readString()
        omiseTaxId = parcel.readString()
        referenceNumber1 = parcel.readString()
        referenceNumber2 = parcel.readString()
        barcode = parcel.readString()
        expiresAt = DateTime.parse(parcel.readString())
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(vaCode)
        dest.writeString(omiseTaxId)
        dest.writeString(referenceNumber1)
        dest.writeString(referenceNumber2)
        dest.writeString(barcode)
        dest.writeString(expiresAt.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<References> {
        override fun createFromParcel(parcel: Parcel): References {
            return References(parcel)
        }

        override fun newArray(size: Int): Array<References?> {
            return arrayOfNulls(size)
        }
    }
}
