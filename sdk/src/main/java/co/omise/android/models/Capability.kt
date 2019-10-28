package co.omise.android.models

import co.omise.android.api.Endpoint
import co.omise.android.api.RequestBuilder
import co.omise.android.models.PaymentMethod.Companion.createSourceTypeMethod
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import okhttp3.HttpUrl
import org.joda.time.DateTime

/**
 * Represents Capabilities object and contains its {@link RequestBuilder}.
 *
 * @see <a href="https://www.omise.co/capability-api">Capabilities API</a>
 */
@Parcelize
data class Capability(
        var banks: List<String>? = null,
        @field:JsonProperty("payment_methods")
        var paymentMethods: List<PaymentMethod>? = null,
        @field:JsonProperty("zero_interest_installments")
        val zeroInterestInstallments: Boolean = false,
        override var modelObject: String? = null,
        override var id: String? = null,
        override var livemode: Boolean = false,
        override var location: String? = null,
        override var created: DateTime? = null,
        override var deleted: Boolean = false
) : Model {

    /**
     * The {@link RequestBuilder} class for retrieving account Capabilities.
     */
    class GetCapabilitiesRequestBuilder : RequestBuilder<Capability>() {

        override fun path(): HttpUrl = buildUrl(Endpoint.API, "capability")

        override fun type(): Class<Capability> = Capability::class.java
    }

    companion object {

        /**
         * The helper function for creating an instance of [Capability].
         * @param allowCreditCard allow to create a [Token] with a credit card or not. Default is true.
         * @param sourceTypes list of [SourceType] that allow to create a [Source].
         * @return an instance of [Capability] with specific configuration.
         */
        @JvmStatic
        fun create(allowCreditCard: Boolean = true, sourceTypes: List<SourceType>): Capability {
            val paymentMethods = sourceTypes.map(::createSourceTypeMethod).toMutableList()

            if (allowCreditCard) {
                paymentMethods.add(PaymentMethod.createCreditCardMethod())
            }

            return Capability(paymentMethods = paymentMethods)
        }
    }
}
