package co.omise.android.models

import android.os.Parcel
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.android.parcel.Parceler

/**
 * Represents charge.status field.
 *
 * @see [Charge API](https://www.omise.co/charges-api)
 */
sealed class ChargeStatus(
    @JsonValue open val value: String,
) {
    object Successful : ChargeStatus("successful")

    object Pending : ChargeStatus("pending")

    object Reversed : ChargeStatus("reversed")

    object Expired : ChargeStatus("expired")

    object Failed : ChargeStatus("failed")

    object Unknown : ChargeStatus("unknown")

    companion object {
        @JsonCreator
        @JvmStatic
        fun creator(name: String?): ChargeStatus =
            when (name) {
                "successful" -> Successful
                "pending" -> Pending
                "reversed" -> Reversed
                "expired" -> Expired
                "failed" -> Failed
                else -> Unknown
            }
    }
}

object ChargeStatusParceler : Parceler<ChargeStatus> {
    override fun create(parcel: Parcel): ChargeStatus {
        return ChargeStatus.creator(parcel.readString())
    }

    override fun ChargeStatus.write(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeString(value)
    }
}
