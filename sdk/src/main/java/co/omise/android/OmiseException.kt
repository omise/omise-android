package co.omise.android

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.lang.Exception

/**
 * This class reserve for passing the Exception from Activity/Fragment through [Intent].
 */
@Parcelize
class OmiseException(
    override val message: String,
    override val cause: Throwable? = null,
) : Exception(message, cause), Parcelable
