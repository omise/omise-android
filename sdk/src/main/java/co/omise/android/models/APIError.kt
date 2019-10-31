package co.omise.android.models

/**
 * Represents Error object.
 *
 * @see <a href="https://www.omise.co/errors-api">Errors API</a>
 */
data class APIError(
        val location: String? = null,
        val code: String? = null,
        override val message: String? = null
) : Error()
