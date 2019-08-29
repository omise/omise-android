package co.omise.android.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "object", visible = true)
@JsonTypeIdResolver(ModelTypeResolver::class)
@Parcelize
open class Model(
        @field:JsonProperty("object")
        val modelObject: String? = null,
        val id: String? = null,
        @field:JsonProperty("livemode")
        val livemode: Boolean = false,
        val location: String? = null,
        @field:JsonProperty("created_at")
        val created: DateTime? = null,
        @field:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        val deleted: Boolean = false
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        val otherModel = other as Model
        return otherModel.id == id &&
                otherModel.modelObject == modelObject &&
                otherModel.livemode == livemode &&
                otherModel.location == location &&
                otherModel.created?.millis == created?.millis &&
                otherModel.deleted == deleted
    }

    override fun hashCode(): Int {
        var result = modelObject?.hashCode() ?: 0
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + livemode.hashCode()
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + (created?.hashCode() ?: 0)
        result = 31 * result + deleted.hashCode()
        return result
    }
}
