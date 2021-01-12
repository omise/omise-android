package co.omise.android.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


sealed class AuthoringPaymentResult : Parcelable {
    @Parcelize
    data class ThreeDS1Completed(val returnedUrl: String) : AuthoringPaymentResult()

    @Parcelize
    data class ThreeDS2Completed(val sdkTransID: String, val transStatus: String) : AuthoringPaymentResult()

    @Parcelize
    data class Failure(val throwable: Throwable) : AuthoringPaymentResult()
}
