package co.omise.android.models

import android.os.Parcelable
import co.omise.android.api.Endpoint
import co.omise.android.api.RequestBuilder
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import okhttp3.HttpUrl

/**
 * Represents Capabilities object and contains its {@link RequestBuilder}.
 *
 * @see <a href="https://www.omise.co/capability-api">Capabilities API</a>
 */
@Parcelize
data class Capability(
        val banks: List<String>? = null,
        @field:JsonProperty("payment_methods")
        val paymentMethods: List<PaymentMethod>? = null,
        @field:JsonProperty("zero_interest_installments")
        val zeroInterestInstallments: Boolean = false
) : Model(), Parcelable {

    /**
     * The {@link RequestBuilder} class for retrieving account Capabilities.
     */
    class GetCapabilitiesRequestBuilder : RequestBuilder<Capability>() {

        override fun path(): HttpUrl = buildUrl(Endpoint.API, "capability")

        override fun type(): Class<Capability> = Capability::class.java
    }
}
