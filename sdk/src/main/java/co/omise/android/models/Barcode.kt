package co.omise.android.models

import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime

@Parcelize
data class Barcode(
    val type: String? = null,
    val image: Document? = null,
    override var modelObject: String? = null,
    override var id: String? = null,
    override var livemode: Boolean = false,
    override var location: String? = null,
    override var created: DateTime? = null,
    override var deleted: Boolean = false,
) : Model
