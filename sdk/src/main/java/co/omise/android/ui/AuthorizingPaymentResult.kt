package co.omise.android.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * The result from the [AuthorizingPaymentResult].
 */
sealed class AuthorizingPaymentResult : Parcelable {
    /**
     * The completion result that authorized with the 3D Secure version 1.
     * @param returnedUrl the returned URL.
     */
    @Parcelize
    data class ThreeDS1Completed(val returnedUrl: String) : AuthorizingPaymentResult()

    /**
     * The completion result that authorized with the 3D Secure version 2.
     * @param sdkTransID SDK transaction ID.
     * @param transStatus the transaction status that received from ACS. Y=Authentication successful, N=Not Authenticated.
     */
    @Parcelize
    data class ThreeDS2Completed(val sdkTransID: String, val transStatus: String) : AuthorizingPaymentResult()

    /**
     * The failure result that occurred from the authorizing process.
     */
    @Parcelize
    data class Failure(val throwable: Throwable) : AuthorizingPaymentResult()
}
