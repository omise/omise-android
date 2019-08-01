package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonCreator

sealed class FlowType(
        val name: String
) : Model() {

    object Redirect : FlowType("redirect")
    object Offline : FlowType("offline")

    companion object {
        @JsonCreator
        @JvmStatic
        private fun creator(name: String): FlowType? {
            return FlowType::class.sealedSubclasses.find {
                it.simpleName == name
            }?.objectInstance
        }
    }
}
