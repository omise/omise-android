package co.omise.android.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import org.joda.time.DateTime

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