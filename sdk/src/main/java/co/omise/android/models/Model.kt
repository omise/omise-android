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
        var modelObject: String? = null,
        var id: String? = null,
        @field:JsonProperty("livemode")
        var livemode: Boolean = false,
        var location: String? = null,
        @field:JsonProperty("created_at")
        var created: DateTime? = null,
        @field:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        var deleted: Boolean = false
) : Parcelable
