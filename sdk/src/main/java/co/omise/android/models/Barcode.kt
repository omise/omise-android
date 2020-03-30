package co.omise.android.models

import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime


@Parcelize
data class Barcode(
        val type: String,
        val image: Document,
        override var modelObject: String?,
        override var id: String?,
        override var livemode: Boolean,
        override var location: String?,
        override var created: DateTime?,
        override var deleted: Boolean
) : Model