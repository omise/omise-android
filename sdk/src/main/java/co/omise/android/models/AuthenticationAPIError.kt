package co.omise.android.models

/**
 * Represents on authentication API error.
 */
internal data class AuthenticationAPIError(
        val status: Authentication.AuthenticationStatus = Authentication.AuthenticationStatus.FAILED,
        override val message: String? = null
) : Error()
