package co.omise.android.models

import org.joda.time.DateTime

class APIError(
        @JvmField
        val location: String? = null,
        @JvmField
        val code: String? = null,
        override
        val message: String? = null,
        @JvmField val created: DateTime? = null
) : Error()
