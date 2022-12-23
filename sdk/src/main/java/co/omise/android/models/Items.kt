package co.omise.android.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    val sku: String? = null,
    val category: String? = null,
    val name: String? = null,
    val quantity: String? = null,
    val amount: String? = null,
    @field:JsonProperty("item_uri")
    val itemUri: String? = null,
    @field:JsonProperty("image_uri")
    val imageUri: String? = null,
    val brand: String? = null,
) : Parcelable
