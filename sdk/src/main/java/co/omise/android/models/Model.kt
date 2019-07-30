package co.omise.android.models

import android.os.Parcel
import android.os.Parcelable

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver

import org.joda.time.DateTime

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "object", visible = true)
@JsonTypeIdResolver(ModelTypeResolver::class)
open class Model() : Parcelable {
    @JvmField
    @field:JsonProperty("object")
    var modelObject: String? = null
    @JvmField
    var id: String? = null
    @JvmField
    @field:JsonProperty("livemode")
    var livemode: Boolean = false
    @JvmField
    var location: String? = null
    @JvmField
    @field:JsonProperty("created_at")
    var created: DateTime? = null
    @JvmField
    @field:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var deleted: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        livemode = parcel.readByte() != 0.toByte()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeInt(if (livemode) 1 else 0)
        dest.writeString(location)
        dest.writeSerializable(created)
        dest.writeInt(if (deleted) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<Model> {
        override fun createFromParcel(parcel: Parcel): Model {
            return Model(parcel)
        }

        override fun newArray(size: Int): Array<Model?> {
            return arrayOfNulls(size)
        }
    }
}
