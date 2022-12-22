package co.omise.android.models

import co.omise.android.api.Endpoint
import co.omise.android.api.RequestBuilder
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import okhttp3.HttpUrl
import okhttp3.RequestBody
import org.joda.time.DateTime
import java.io.IOException

/**
 * Represents Source object and contains its [RequestBuilder].
 *
 * @see [Sources API](https://www.omise.co/sources-api)
 */
@Parcelize
@TypeParceler<SourceType, SourceTypeParceler>()
@TypeParceler<FlowType, FlowTypeParceler>()
@TypeParceler<ChargeStatus, ChargeStatusParceler>()
data class Source(
    val type: SourceType = SourceType.Unknown(null),
    val flow: FlowType = FlowType.Unknown(null),
    val amount: Long = 0,
    val currency: String? = null,
    val barcode: String? = null,
    val references: References? = null,
    @field:JsonProperty("store_id")
    val storeId: String? = null,
    @field:JsonProperty("store_name")
    val storeName: String? = null,
    @field:JsonProperty("terminal_id")
    val terminalId: String? = null,
    val name: String? = null,
    val email: String? = null,
    val bank: String? = null,
    @field:JsonProperty("platform_type")
    val platformType: String? = null,
    @field:JsonProperty("phone_number")
    val phoneNumber: String? = null,
    @field:JsonProperty("mobile_number")
    val mobileNumber: String? = null,
    @field:JsonProperty("shipping")
    val shipping: Shipping? = null,
    @field:JsonProperty("billing")
    val billing: Billing? = null,
    @field:JsonProperty("items")
    val items: ArrayList<Item>? = ArrayList(),
    @field:JsonProperty("installment_term")
    val installmentTerm: Int? = null,
    @field:JsonProperty("scannable_code")
    val scannableCode: Barcode? = null,
    @field:JsonProperty("zero_interest_installments")
    val zeroInterestInstallments: Boolean? = null,
    @field:JsonProperty("charge_status")
    val chargeStatus: ChargeStatus = ChargeStatus.Unknown,
    override var modelObject: String? = null,
    override var id: String? = null,
    override var livemode: Boolean = false,
    override var location: String? = null,
    override var created: DateTime? = null,
    override var deleted: Boolean = false
) : Model {

    /**
     * The [RequestBuilder] class for creating a Source.
     */
    class CreateSourceRequestBuilder(
        @JsonProperty
        val amount: Long,
        @JsonProperty
        val currency: String,
        val type: SourceType
    ) : RequestBuilder<Source>() {

        @JsonProperty
        private var description: String? = null

        @JsonProperty
        private var barcode: String? = null

        @JsonProperty("store_id")
        private var storeId: String? = null

        @JsonProperty("store_name")
        private var storeName: String? = null

        @JsonProperty("terminal_id")
        private var terminalId: String? = null

        @JsonProperty("name")
        private var name: String? = null

        @JsonProperty("email")
        private var email: String? = null

        @JsonProperty("bank")
        private var bank: String? = null

        @JsonProperty("platform_type")
        private var platformType: String? = "android"

        @JsonProperty("phone_number")
        private var phoneNumber: String? = null

        @JsonProperty("shipping")
        private var shipping: Shipping? = null

        @JsonProperty("billing")
        private var billing: Billing? = null

        @JsonProperty("items")
        private var items: List<Item>? = null

        @JsonProperty("installment_term")
        private var installmentTerm: Int? = null

        @JsonProperty("zero_interest_installments")
        private var zeroInterestInstallments: Boolean? = null

        override fun method(): String {
            return POST
        }

        override fun path(): HttpUrl {
            return buildUrl(Endpoint.API, "sources")
        }

        @Throws(IOException::class)
        override fun payload(): RequestBody? {
            return serialize()
        }

        override fun type(): Class<Source> {
            return Source::class.java
        }

        fun description(description: String): CreateSourceRequestBuilder {
            this.description = description
            return this
        }

        fun barcode(barcode: String): CreateSourceRequestBuilder {
            this.barcode = barcode
            return this
        }

        fun storeId(storeId: String): CreateSourceRequestBuilder {
            this.storeId = storeId
            return this
        }

        fun storeName(storeName: String): CreateSourceRequestBuilder {
            this.storeName = storeName
            return this
        }

        fun terminalId(terminalId: String): CreateSourceRequestBuilder {
            this.terminalId = terminalId
            return this
        }

        fun name(name: String): CreateSourceRequestBuilder {
            this.name = name
            return this
        }

        fun email(email: String): CreateSourceRequestBuilder {
            this.email = email
            return this
        }

        fun bank(bank: String): CreateSourceRequestBuilder {
            this.bank = bank
            return this
        }

        fun platformType(platformType: String): CreateSourceRequestBuilder {
            this.platformType = platformType
            return this
        }

        fun phoneNumber(phoneNumber: String): CreateSourceRequestBuilder {
            this.phoneNumber = phoneNumber
            return this
        }

        fun shipping(shipping: Shipping): CreateSourceRequestBuilder {
            this.shipping = shipping
            return this
        }

        fun billing(billing: Billing): CreateSourceRequestBuilder {
            this.billing = billing
            return this
        }

        fun items(items: List<Item>): CreateSourceRequestBuilder{
            this.items = items
            return this
        }

        fun installmentTerm(installmentTerm: Int): CreateSourceRequestBuilder {
            this.installmentTerm = installmentTerm
            return this
        }

        fun zeroInterestInstallments(zeroInterestInstallments: Boolean): CreateSourceRequestBuilder {
            this.zeroInterestInstallments = zeroInterestInstallments
            return this
        }
    }

    /**
     * The [RequestBuilder] class for retrieving a particular Source.
     * @property id the Source ID.
     */
    class GetSourceRequestBuilder(val id: String) : RequestBuilder<Source>() {
        override fun path(): HttpUrl {
            return buildUrl(Endpoint.API, "sources", id)
        }

        override fun method(): String {
            return GET
        }

        override fun type(): Class<Source> {
            return Source::class.java
        }
    }
}
