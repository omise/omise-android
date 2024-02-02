package co.omise.android.extensions

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable

internal inline fun <reified T : Parcelable> Bundle.getParcelableArrayCompat(key: String?) = when {
    // https://stackoverflow.com/questions/72571804/getserializableextra-and-getparcelableextra-are-deprecated-what-is-the-alternat/73543350#73543350
    SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableArray(key,T::class.java)?.mapNotNull { it }?.toTypedArray() ?: emptyArray()
    else -> @Suppress("DEPRECATION")
    getParcelableArray(key)?.mapNotNull { it as? T }?.toTypedArray() ?: emptyArray()
}

internal inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String?): T? =
    when {
        SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            getParcelable(key, T::class.java)
        }
        else -> @Suppress("DEPRECATION")
        getParcelable(key) as? T
    }
