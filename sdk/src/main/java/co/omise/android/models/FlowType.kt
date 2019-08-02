package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonCreator

sealed class FlowType(
        val name: String
) {

    object Redirect : FlowType("redirect")
    object Offline : FlowType("offline")

    companion object {
        @JsonCreator
        @JvmStatic
        fun creator(name: String): FlowType? {
            return FlowType::class.sealedSubclasses.firstOrNull {
                it.simpleName?.toLowerCase() == name.toLowerCase()
            }?.objectInstance
        }
    }
}
