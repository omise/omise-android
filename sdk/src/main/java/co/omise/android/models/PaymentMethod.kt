package co.omise.android.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethod(
        val name: String? = null,
        val currencies: List<String>? = null,
        @field:JsonProperty("card_brands")
        val cardBrands: List<String>? = null,
        @field:JsonProperty("installment_terms")
        val installmentTerms: List<Int>? = null
) : Model(), Parcelable
