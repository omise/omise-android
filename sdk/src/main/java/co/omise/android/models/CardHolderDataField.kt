package co.omise.android.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

enum class CardHolderDataField {
    EMAIL,
    PHONE_NUMBER,
}

// Parcelable wrapper for the array/list
@Parcelize
data class CardHolderDataList(
    val fields: ArrayList<CardHolderDataField>,
) : Parcelable
