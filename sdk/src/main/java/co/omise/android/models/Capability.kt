package co.omise.android.models

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
) : Model() {

    /**
     * The {@link RequestBuilder} class for retrieving account Capabilities.
     */
    class GetCapabilitiesRequestBuilder : RequestBuilder<Capability>() {

        override fun path(): HttpUrl = buildUrl(Endpoint.API, "capability")

        override fun type(): Class<Capability> = Capability::class.java
    }
}
