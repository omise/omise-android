package co.omise.android.api

import co.omise.android.models.Model
import okhttp3.HttpUrl
import okhttp3.RequestBody

abstract class RequestBuilder<T : Model> {

    fun build(): Request<T> {
        return Request(method(), path(), payload(), type())
    }

    open fun method(): String {
        return GET
    }

    protected abstract fun path(): HttpUrl

    open fun payload(): RequestBody? {
        //Has to be null as it would fail for GET requests
        return null
    }

    protected abstract fun type(): Class<T>

    companion object {
        const val POST = "POST"
        const val GET = "GET"
        const val PATCH = "PATCH"
        const val DELETE = "DELETE"
    }
}
