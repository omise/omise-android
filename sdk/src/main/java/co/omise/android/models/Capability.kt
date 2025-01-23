package co.omise.android.models

import co.omise.android.api.Endpoint
import co.omise.android.api.RequestBuilder
import co.omise.android.models.PaymentMethod.Companion.createSourceTypeMethod
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
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
    var paymentMethods: MutableList<PaymentMethod>? = null,
    @field:JsonProperty("tokenization_methods")
    var tokenizationMethods: List<String>? = null,
    @field:JsonProperty("zero_interest_installments")
    var zeroInterestInstallments: Boolean = false,
    @field:JsonProperty("limits")
    var limits: Limits? = null,
    @field:JsonProperty
    val country: String? = null,
    override var modelObject: String? = null,
    override var id: String? = null,
    override var livemode: Boolean = false,
    override var location: String? = null,
    override var created: DateTime? = null,
    override var deleted: Boolean = false,
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
         * The helper function for creating an instance of the [Capability] class.
         *
         * @param allowCreditCard allow to create a [Token] with a credit card or not. Default is true.
         * @param sourceTypes list of [SourceType] that allow to create a [Source].
         * @param tokenizationMethods list of [TokenizationMethod] that we can create a [Token] with.
         * @param zeroInterestInstallments whether merchant absorbs interest for installment payments.
         *
         * @return an instance of [Capability] with specific configuration.
         */
        @JvmStatic
        fun create(
            allowCreditCard: Boolean = true,
            sourceTypes: List<SourceType>,
            tokenizationMethods: List<TokenizationMethod> = emptyList(),
            zeroInterestInstallments: Boolean = false,
        ): Capability {
            val paymentMethods = mutableListOf<PaymentMethod>()

            if (allowCreditCard) {
                paymentMethods.add(PaymentMethod.createCreditCardMethod())
            }

            paymentMethods.addAll(sourceTypes.map(::createSourceTypeMethod))
            return Capability(
                paymentMethods = paymentMethods,
                zeroInterestInstallments = zeroInterestInstallments,
                limits = Limits(InstallmentAmount(200000L)),
                tokenizationMethods = tokenizationMethods.map { tokenizationMethod -> tokenizationMethod.name!! },
            )
        }
    }
}

val Capability.installmentMethods: List<PaymentMethod>
    get() =
        this.paymentMethods
            ?.filter { (it.backendType as? BackendType.Source)?.sourceType is SourceType.Installment }
            .orEmpty()

val Capability.internetBankingMethods: List<PaymentMethod>
    get() =
        this.paymentMethods
            ?.filter { (it.backendType as? BackendType.Source)?.sourceType is SourceType.InternetBanking }
            .orEmpty()

val Capability.mobileBankingMethods: List<PaymentMethod>
    get() =
        this.paymentMethods
            ?.filter { (it.backendType as? BackendType.Source)?.sourceType is SourceType.MobileBanking }
            .orEmpty()
