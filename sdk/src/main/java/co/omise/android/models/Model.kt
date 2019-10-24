package co.omise.android.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import org.joda.time.DateTime

/**
 * Model is a base class that all model classes are extended from and contains fields
 * that are shared between all models.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "object", visible = true)
@JsonTypeIdResolver(ModelTypeResolver::class)
interface Model : Parcelable {
    @get:JsonProperty("object")
    var modelObject: String?
    var id: String?
    var livemode: Boolean
    var location: String?
    @get:JsonProperty("created_at")
    var created: DateTime?
    var deleted: Boolean
}