package co.omise.android.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import org.joda.time.DateTime

/* Due to some response from Omise API, the object field is not always present. So this restriction cannot be used.
 @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "object", visible = true)
 */

/**
 * Model is a base class from which all model classes are extended and contains fields
 * that are shared between all models.
 */
@JsonTypeIdResolver(ModelTypeResolver::class)
interface Model : Parcelable {
    @get:JsonProperty("object")
    val modelObject: String?
    val id: String?
    val livemode: Boolean
    val location: String?

    @get:JsonProperty("created_at")
    val created: DateTime?
    val deleted: Boolean
}
