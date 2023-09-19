//package co.omise.android.models
//
//import android.os.Parcel
//import android.os.Parcelable
//import co.omise.android.api.RequestBuilder
//import com.fasterxml.jackson.annotation.JsonProperty
//import kotlinx.android.parcel.Parcelize
//import okhttp3.HttpUrl
//import okhttp3.HttpUrl.Companion.toHttpUrl
//import okhttp3.RequestBody
//import java.io.IOException
//
//@Parcelize
//data class Authentication(
//    val status: AuthenticationStatus,
//    val areq: AReq?,
//    val ares: ARes?
//) : Model {
//
//    enum class AuthenticationStatus(val value: String) {
//        @JsonProperty("success")
//        SUCCESS("success"),
//
//        @JsonProperty("failed")
//        FAILED("failed"),
//
//        @JsonProperty("challenge_v1")
//        CHALLENGE_V1("challenge_v1"),
//
//        @JsonProperty("challenge")
//        CHALLENGE("challenge")
//    }
//
//    data class AReq(val sdkReferenceNumber: String)
//
//    data class ARes(
//        val messageVersion: String,
//        val threeDSServerTransID: String,
//        val acsTransID: String,
//        val sdkTransID: String,
//        val acsSignedContent: String? = null
//    )
//
//    class AuthenticationRequestBuilder(
//        @field:JsonProperty("areq")
//        val areq: AReq,
//        @field:JsonProperty("device_info")
//        val deviceInfo: Map<String, Any>,
//        @field:JsonProperty("device_type")
//        val deviceType: String = "Android",
//    ) : RequestBuilder<Authentication>() {
//        override fun path(): HttpUrl {
//            return "".toHttpUrl()
//        }
//
//        @Throws(IOException::class)
//        override fun payload(): RequestBody? {
//            return serialize()
//        }
//
//        override fun method(): String {
//            return POST
//        }
//
//        override fun type(): Class<Authentication> {
//            return Authentication::class.java
//        }
//
//        data class AReq(
//            val sdkAppID: String? = null,
//            val sdkEphemPubKey: EphemPubKey? = null,
//            val sdkTransID: String? = null,
//            val sdkMaxTimeout: Int? = null
//        )
//
//        data class EphemPubKey(
//            val kty: String,
//            val crv: String,
//            val x: String,
//            val y: String
//        )
//    }
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        super.writeToParcel(parcel, flags)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<Authentication> {
//        override fun createFromParcel(parcel: Parcel): Authentication {
//            return Authentication(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Authentication?> {
//            return arrayOfNulls(size)
//        }
//    }
//}
