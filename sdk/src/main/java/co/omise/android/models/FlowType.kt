package co.omise.android.models

import android.annotation.SuppressLint
import android.os.Parcel
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.android.parcel.Parceler

/**
 * Represents Source Flow object.
 *
 * @see [Sources API](https://www.omise.co/sources-api)
 */
sealed class FlowType(
    @JsonValue open val name: String?,
) {
    object Redirect : FlowType("redirect")

    object Offline : FlowType("offline")

    data class Unknown(override val name: String?) : FlowType(name)

    companion object {
        @SuppressLint("DefaultLocale")
        @JsonCreator
        @JvmStatic
        fun creator(name: String?): FlowType =
            when (name) {
                "redirect" -> Redirect
                "offline" -> Offline
                else -> Unknown(name)
            }
    }
}

object FlowTypeParceler : Parceler<FlowType> {
    override fun create(parcel: Parcel): FlowType {
        return FlowType.creator(parcel.readString())
    }

    override fun FlowType.write(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeString(name)
    }
}
