package co.omise.android.api.exceptions

class RedirectionException : Throwable() {
    override val message: String?
        get() = "Redirection is not allowed."
}