package co.omise.android.models

import android.os.Parcel
import android.os.Parcelable
import co.omise.android.api.Endpoint
import co.omise.android.api.RequestBuilder
import okhttp3.HttpUrl
import org.json.JSONObject

/**
 * Represents Capabilities object and contains its {@link RequestBuilder}.
 *
 * @see <a href="https://www.omise.co/capability-api">Capabilities API</a>
 */
class Capability : Model {
    @JvmField
    var banks: List<String>? = null
    @JvmField
    var paymentMethods: List<PaymentMethod>? = null
    @JvmField
    var zeroInterestInstallments: Boolean = false


    constructor(rawJson: String) : this(JSONObject(rawJson))

    private constructor(json: JSONObject) : super(json) {
        banks = JSON.stringList(json, "banks")
        paymentMethods = JSON.modelList(json, "payment_methods", PaymentMethod::class.java)
        zeroInterestInstallments = JSON.bool(json, "zero_interest_installments")
    }

    /**
     * The {@link RequestBuilder} class for retrieving account Capabilities.
     */
    class GetCapabilitiesRequestBuilder : RequestBuilder<Capability>() {

        override fun path(): HttpUrl = buildUrl(Endpoint.API, "capability")

        override fun type(): Class<Capability> = Capability::class.java
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeStringList(banks)
        parcel.writeTypedList(paymentMethods)
        parcel.writeByte(if (zeroInterestInstallments) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Capability> {
        override fun createFromParcel(parcel: Parcel): Capability {
            return Capability(parcel.readString())
        }

        override fun newArray(size: Int): Array<Capability?> {
            return arrayOfNulls(size)
        }
    }
}
