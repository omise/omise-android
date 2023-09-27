package co.omise.android.models

import android.os.Parcelable
import co.omise.android.api.RequestBuilder
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.RequestBody
import org.joda.time.DateTime
import java.io.IOException

// TODO: Change fields to non-nullable and remove default value after adding jackson-module-kotlin
@Parcelize
internal data class Authentication(
    val status: AuthenticationStatus = AuthenticationStatus.FAILED,
    val areq: AReq? = null,
    val ares: ARes? = null,
    override val modelObject: String? = null,
    override val id: String? = null,
    override val livemode: Boolean = false,
    override val location: String? = null,
    override val created: DateTime? = null,
    override val deleted: Boolean = false,
) : Model {

    enum class AuthenticationStatus(val value: String) {
        @JsonProperty("success")
        SUCCESS("success"),

        @JsonProperty("failed")
        FAILED("failed"),

        @JsonProperty("challenge_v1")
        CHALLENGE_V1("challenge_v1"),

        @JsonProperty("challenge")
        CHALLENGE("challenge")
    }

    // TODO: Change fields to non-nullable and remove default value after adding jackson-module-kotlin
    @Parcelize
    data class AReq(val sdkReferenceNumber: String? = null) : Parcelable

    // TODO: Change fields to non-nullable and remove default value after adding jackson-module-kotlin
    @Parcelize
    data class ARes(
        val messageVersion: String? = null,
        val threeDSServerTransID: String? = null,
        val acsTransID: String? = null,
        val sdkTransID: String? = null,
        val acsSignedContent: String? = null,
    ) : Parcelable

    class AuthenticationRequestBuilder(
        @JsonIgnore
        val authorizeUrl: String,
        @field:JsonProperty("areq")
        val areq: AReq,
        @field:JsonProperty("device_info")
        // TODO: Change type to String after adding authorize endpoint has been changed.
        // val deviceInfo: String,
        val deviceInfo: Map<String, Any>,
        @field:JsonProperty("device_type")
        val deviceType: String = "Android",
    ) : RequestBuilder<Authentication>() {

        override fun path(): HttpUrl {
            return authorizeUrl.toHttpUrl()
        }

        @Throws(IOException::class)
        override fun payload(): RequestBody? {
            return serialize()
        }

        override fun method(): String {
            return POST
        }

        override fun type(): Class<Authentication> {
            return Authentication::class.java
        }

        data class AReq(
            val sdkAppID: String,
            val sdkEphemPubKey: SdkEphemPubKey,
            val sdkTransID: String,
            val sdkMaxTimeout: Int,
        )

        // TODO: Change fields to non-nullable and remove default value after adding jackson-module-kotlin
        data class SdkEphemPubKey(
            val kty: String? = null,
            val x: String? = null,
            val y: String? = null,
            val crv: String? = null,
        )
    }
}
