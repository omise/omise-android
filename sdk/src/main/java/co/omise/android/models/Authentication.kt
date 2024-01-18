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
    enum class AuthenticationStatus(val value: String, val message: String? = null) {
        @JsonProperty("success")
        SUCCESS("success"),

        @JsonProperty("failed")
        FAILED("failed", "Authentication failed"),

        @JsonProperty("challenge_v1")
        CHALLENGE_V1("challenge_v1"),

        @JsonProperty("challenge")
        CHALLENGE("challenge"),
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

    class AuthenticationRequestBuilder : RequestBuilder<Authentication>() {
        @JsonIgnore
        private var authorizeUrl: String? = null

        @JsonProperty("areq")
        private var areq: AReq? = null

        @JsonProperty("device_info")
        private var deviceInfo: String? = null

        @JsonProperty("device_type")
        private val deviceType: String = "Android"

        fun authorizeUrl(authorizeUrl: String): AuthenticationRequestBuilder {
            this.authorizeUrl = authorizeUrl
            return this
        }

        fun areq(areq: AReq): AuthenticationRequestBuilder {
            this.areq = areq
            return this
        }

        fun deviceInfo(deviceInfo: String): AuthenticationRequestBuilder {
            this.deviceInfo = deviceInfo
            return this
        }

        override fun path(): HttpUrl {
            return authorizeUrl?.toHttpUrl() ?: throw IllegalArgumentException("authorizeUrl is required.")
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

        override fun errorType(): Class<Error> {
            return AuthenticationAPIError::class.java as Class<Error>
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
