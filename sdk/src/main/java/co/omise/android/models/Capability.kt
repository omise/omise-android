package co.omise.android.models

import android.os.Parcel
import android.os.Parcelable
import co.omise.android.api.Endpoint
import co.omise.android.api.RequestBuilder
import com.fasterxml.jackson.annotation.JsonProperty
import okhttp3.HttpUrl

/**
 * Represents Capabilities object and contains its {@link RequestBuilder}.
 *
 * @see <a href="https://www.omise.co/capability-api">Capabilities API</a>
 */
data class Capability(
        @JvmField
        var banks: List<String>? = null,
        @JvmField
        @field:JsonProperty("payment_methods")
        var paymentMethods: List<PaymentMethod>? = null,
        @JvmField
        @field:JsonProperty("zero_interest_installments")
        var zeroInterestInstallments: Boolean = false
) : Model(), Parcelable {

    constructor(parcel: Parcel) : this() {
        banks = parcel.createStringArrayList()
        paymentMethods = parcel.createTypedArrayList(PaymentMethod.CREATOR)
        zeroInterestInstallments = parcel.readInt() == 1
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeStringArray(banks?.toTypedArray())
        dest.writeParcelableArray(paymentMethods?.toTypedArray(), 0)
        dest.writeInt(if (zeroInterestInstallments) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<Capability> {
        override fun createFromParcel(parcel: Parcel): Capability {
            return Capability(parcel)
        }

        override fun newArray(size: Int): Array<Capability?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * The {@link RequestBuilder} class for retrieving account Capabilities.
     */
    class GetCapabilitiesRequestBuilder : RequestBuilder<Capability>() {

        override fun path(): HttpUrl = buildUrl(Endpoint.API, "capability")

        override fun type(): Class<Capability> = Capability::class.java
    }
}
