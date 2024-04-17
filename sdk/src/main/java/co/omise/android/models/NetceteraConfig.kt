import co.omise.android.api.RequestBuilder
import co.omise.android.models.Model
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.joda.time.DateTime

@Parcelize
internal data class NetceteraConfig(
    @field:JsonProperty("identifier") val identifier: String? = null,
    @field:JsonProperty("device_info_encryption_alg") val deviceInfoEncryptionAlg: String? = null,
    @field:JsonProperty("device_info_encryption_enc") val deviceInfoEncryptionEnc: String? = null,
    @field:JsonProperty("device_info_encryption_cert_pem") val deviceInfoEncryptionCertPem: String? = null,
    @field:JsonProperty("directory_server_id") val directoryServerId: String? = null,
    @field:JsonProperty("key") val key: String? = null,
    @field:JsonProperty("message_version") val messageVersion: String? = null,
    override val modelObject: String? = null,
    override val id: String? = null,
    override val livemode: Boolean = false,
    override val location: String? = null,
    override val created: DateTime? = null,
    override val deleted: Boolean = false,
) : Model {
    class NetceteraConfigRequestBuilder : RequestBuilder<NetceteraConfig>() {
        private var configUrl: String? = null

        fun configUrl(configUrl: String): NetceteraConfigRequestBuilder {
            this.configUrl = configUrl
            return this
        }

        override fun path(): HttpUrl {
            return configUrl?.toHttpUrl() ?: throw IllegalArgumentException("configUrl is required.")
        }

        override fun method(): String {
            return GET
        }

        override fun type(): Class<NetceteraConfig> {
            return NetceteraConfig::class.java
        }
    }
}
