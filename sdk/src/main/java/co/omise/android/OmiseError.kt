package co.omise.android

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * This class reserve for passing the Exception from Activity/Fragment through [Intent].
 */
@Parcelize
data class OmiseError(val message: String, val cause: Throwable?) : Parcelable
