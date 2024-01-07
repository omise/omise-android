package co.omise.android.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bank(
    var name: String? = null,
    var code: String? = null,
    var active: Boolean? = false,
) : Parcelable
