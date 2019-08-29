package co.omise.android.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

/**
 * Represents Source References object.
 *
 * @see [Sources API](https://www.omise.co/sources-api)
 */
@Parcelize
data class References(
        @field:JsonProperty("va_code")
        val vaCode: String? = null,
        @field:JsonProperty("omise_tax_id")
        val omiseTaxId: String? = null,
        @field:JsonProperty("reference_number_1")
        val referenceNumber1: String? = null,
        @field:JsonProperty("reference_number_2")
        val referenceNumber2: String? = null,
        val barcode: String? = null,
        @field:JsonProperty("expires_at")
        val expiresAt: DateTime? = null
) : Model(), Parcelable
