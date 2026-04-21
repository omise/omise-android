package co.omise.android.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Parcelize
data class Limits(
    @field:JsonProperty("installment_amount")
    val installmentAmount: InstallmentAmount? = null,
) : Parcelable

@Parcelize
data class InstallmentAmount(
    @field:JsonProperty("min")
    val min: Long? = null,
) : Parcelable
