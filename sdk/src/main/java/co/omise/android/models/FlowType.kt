package co.omise.android.models

import android.annotation.SuppressLint
import android.os.Parcel
import com.fasterxml.jackson.annotation.JsonCreator
import kotlinx.android.parcel.Parceler

/**
 * Represents Source Flow object.
 *
 * @see [Sources API](https://www.omise.co/sources-api)
 */
sealed class FlowType(
        val name: String?
) {

    object Redirect : FlowType("redirect")
    object Offline : FlowType("offline")
    object Unknown : FlowType(null)

    companion object {
        @SuppressLint("DefaultLocale")
        @JsonCreator
        @JvmStatic
        fun creator(name: String?): FlowType? {
            return FlowType::class.sealedSubclasses.firstOrNull {
                it.simpleName?.toLowerCase() == name?.toLowerCase()
            }?.objectInstance
        }
    }
}

object FlowTypeParceler : Parceler<FlowType> {
    override fun create(parcel: Parcel): FlowType {
        val flowType = FlowType.creator(parcel.readString())
        return flowType ?: FlowType.Unknown
    }

    override fun FlowType.write(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }
}