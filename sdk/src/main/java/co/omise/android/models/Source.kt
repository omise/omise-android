package co.omise.android.models

import android.os.Parcelable
import co.omise.android.api.Endpoint
import co.omise.android.api.RequestBuilder
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import okhttp3.HttpUrl
import okhttp3.RequestBody
import java.io.IOException

/**
 * Represents Source object and contains its [RequestBuilder].
 *
 * @see [Sources API](https://www.omise.co/sources-api)
 */
@Parcelize
@TypeParceler<SourceType, SourceTypeParceler>()
@TypeParceler<FlowType, FlowTypeParceler>()
data class Source(
        var type: SourceType = SourceType.Unknown,
        var flow: FlowType = FlowType.Default,
        var amount: Long = 0,
        var currency: String? = null,
        var barcode: String? = null,
        var references: References? = null,
        @field:JsonProperty("store_id")
        var storeId: String? = null,
        @field:JsonProperty("store_name")
        var storeName: String? = null,
        @field:JsonProperty("terminal_id")
        var terminalId: String? = null,
        var name: String? = null,
        var email: String? = null,
        @field:JsonProperty("phone_number")
        var phoneNumber: String? = null,
        @field:JsonProperty("installment_term")
        var installmentTerm: Int = 0
) : Model(), Parcelable {

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
        @JsonProperty("phone_number")
        private var phoneNumber: String? = null
        @JsonProperty("installment_term")
        private var installmentTerm: Int = 0

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

        fun phoneNumber(phoneNumber: String): CreateSourceRequestBuilder {
            this.phoneNumber = phoneNumber
            return this
        }

        fun installmentTerm(installmentTerm: Int): CreateSourceRequestBuilder {
            this.installmentTerm = installmentTerm
            return this
        }
    }
}
