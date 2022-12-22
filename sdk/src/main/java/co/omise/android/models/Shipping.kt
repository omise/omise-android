package co.omise.android.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Shipping(
    val city: String? = null,
    val country: String? = null,
    @field:JsonProperty("postal_code")
    val postalCode: String? = null,
    val state: String? = null,
    val street1: String? = null,
    val street2: String? = null,
) : Parcelable