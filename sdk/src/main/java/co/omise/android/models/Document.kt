package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime


@Parcelize
data class Document(
        @field:JsonProperty("filename")
        val filename: String? = null,
        @field:JsonProperty("download_uri")
        val downloadUri: String? = null,
        override var modelObject: String?,
        override var id: String?,
        override var livemode: Boolean,
        override var location: String?,
        override var created: DateTime?,
        override var deleted: Boolean
) : Model