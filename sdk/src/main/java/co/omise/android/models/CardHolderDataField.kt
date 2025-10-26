package co.omise.android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class CardHolderDataField {
    EMAIL,
    PHONE_NUMBER,
}

// Parcelable wrapper for the array/list
@Parcelize
data class CardHolderDataList(
    val fields: ArrayList<CardHolderDataField>,
) : Parcelable

fun CardHolderDataField.toFlutterString(): String = when (this) {
    CardHolderDataField.EMAIL -> "email"
    CardHolderDataField.PHONE_NUMBER -> "phoneNumber"
}
