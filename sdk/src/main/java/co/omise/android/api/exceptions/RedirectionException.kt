package co.omise.android.api.exceptions

/**
 * Client Exception object that is thrown if the API response contains a redirection response code.
 */
class RedirectionException : Throwable() {
    override val message: String?
        get() = "Redirection is not allowed."
}
