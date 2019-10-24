package co.omise.android.models

/**
 * Represents Error object.
 *
 * @param location API documentation location.
 * @param code Error code.
 * @param message Error explanation.
 *
 * @see <a href="https://www.omise.co/errors-api">Errors API</a>
 */
data class APIError(
        val location: String? = null,
        val code: String? = null,
        override val message: String? = null
) : Error()
