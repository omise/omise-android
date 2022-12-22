package co.omise.android.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    val sku: String? = null,
    val category: String? = null,
    val name: String? = null,
    val quantity: String? = null,
    val amount: String? = null,
    val item_uri: String? = null,
    val image_uri: String? = null,
    val brand: String? = null,
) : Parcelable