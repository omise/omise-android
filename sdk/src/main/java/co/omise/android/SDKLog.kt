package co.omise.android

import android.util.Log

/**
 * SDKLog is the custom SDK logging class that allows easier logging that is
 * specific to the SDK tag.
 */
internal object SDKLog {
    private const val TAG = "co.omise.android"

    fun d(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }

    fun e(
        message: String,
        e: Throwable,
    ) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, e)
        }
    }

    fun wtf(
        message: String,
        e: Throwable,
    ) {
        if (BuildConfig.DEBUG) {
            Log.wtf(TAG, message, e)
        }
    }
}
