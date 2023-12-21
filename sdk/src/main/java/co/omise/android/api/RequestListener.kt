package co.omise.android.api

import co.omise.android.models.Model

/**
 * Listener for [Request] results.
 */
interface RequestListener<T : Model> {
    /**
     * Invoked when a [Request] succeeds.
     *
     * @param model The Model result.
     */
    fun onRequestSucceed(model: T)

    /**
     * Invoked when a [Request] fails.
     *
     *
     * Possible errors includes [org.json.JSONException] and general [java.io.IOException]
     *
     * @param throwable The error.
     */
    fun onRequestFailed(throwable: Throwable)
}
