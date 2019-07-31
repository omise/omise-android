package co.omise.android.models

import org.joda.time.DateTime

/**
 * Represents Error object.
 *
 * @see <a href="https://www.omise.co/errors-api">Errors API</a>
 */
data class APIError(
        @JvmField
        val location: String? = null,
        @JvmField
        val code: String? = null,
        override
        val message: String? = null,
        @JvmField val created: DateTime? = null
) : Error()
