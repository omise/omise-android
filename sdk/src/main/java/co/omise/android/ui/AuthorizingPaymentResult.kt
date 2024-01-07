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
     * @param status the [TransactionStatus] of the authorization.
     */
    @Parcelize
    data class ThreeDS2Completed(val transStatus: TransactionStatus) : AuthorizingPaymentResult()

    /**
     * The failure result that occurred from the authorizing process.
     * @param throwable if authorization is failed.
     */
    @Parcelize
    data class Failure(val throwable: Throwable) : AuthorizingPaymentResult()
}

enum class TransactionStatus {
    AUTHENTICATED,
    NOT_AUTHENTICATED,
}
