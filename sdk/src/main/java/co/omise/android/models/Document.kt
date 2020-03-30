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
        override var modelObject: String? = null,
        override var id: String? = null,
        override var livemode: Boolean = false,
        override var location: String? = null,
        override var created: DateTime? = null,
        override var deleted: Boolean = false
) : Model