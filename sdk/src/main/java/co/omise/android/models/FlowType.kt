package co.omise.android.models

sealed class FlowType(val name: String) {
    class Redirect : FlowType("redirect")
    class Offline : FlowType("offline")
}
