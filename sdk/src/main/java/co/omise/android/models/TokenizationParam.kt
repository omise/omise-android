package co.omise.android.models

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * CardParam holds all the information about a Card that can be supplied to create
 * a [Token].
 *
 * @see [Token API](https://www.omise.co/tokens-api)
 */
data class TokenizationParam(
        @field:JsonProperty
        val method: String,
        @field:JsonProperty
        val data: String,
)
