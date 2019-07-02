package co.omise.android.api

import co.omise.android.models.Model
import okhttp3.HttpUrl
import okhttp3.RequestBody

abstract class RequestBuilder<T : Model> {

    fun build(): Request<T> {
        return Request(method(), path(), payload(), type())
    }

    private fun method(): String {
        return GET
    }

    protected abstract fun path(): HttpUrl

    private fun payload(): RequestBody? {
        //Has to be null as it would fail for GET requests
        return null
    }

    protected abstract fun type(): Class<T>

    companion object {
        val POST = "POST"
        val GET = "GET"
        val PATCH = "PATCH"
        val DELETE = "DELETE"
    }
}
