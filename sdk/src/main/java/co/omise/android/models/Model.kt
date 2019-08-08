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
    @field:JsonProperty("object")
    var modelObject: String? = null
    var id: String? = null
    @field:JsonProperty("livemode")
    var livemode: Boolean = false
    var location: String? = null
    @field:JsonProperty("created_at")
    var created: DateTime? = null
    @field:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var deleted: Boolean = false

    constructor(parcel: Parcel) : this() {
        modelObject = parcel.readString()
        id = parcel.readString()
        livemode = parcel.readInt() == 1
        location = parcel.readString()
        created = DateTime.parse(parcel.readString())
        deleted = parcel.readInt() == 1
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(modelObject)
        dest.writeString(id)
        dest.writeInt(if (livemode) 1 else 0)
        dest.writeString(location)
        dest.writeString(created.toString())
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
