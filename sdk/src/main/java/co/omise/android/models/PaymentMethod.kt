package co.omise.android.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethod(
        var name: String? = null,
        var currencies: List<String>? = null,
        @field:JsonProperty("card_brands")
        var cardBrands: List<String>? = null,
        @field:JsonProperty("installment_terms")
        var installmentTerms: List<Int>? = null
) : Model(), Parcelable
