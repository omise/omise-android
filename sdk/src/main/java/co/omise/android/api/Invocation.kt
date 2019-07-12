package co.omise.android.api

import android.os.Handler
import co.omise.android.api.exceptions.RedirectionException
import co.omise.android.models.APIError
import co.omise.android.models.Model
import co.omise.android.models.Serializer
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import java.io.IOException

internal class Invocation<T : Model>(
        private val replyHandler: Handler,
        private val httpClient: OkHttpClient,
        private val request: co.omise.android.api.Request<T>,
        private val listener: RequestListener<T>,
        private val serializer: Serializer = Serializer()
) {

    fun invoke() {
        try {
            val call = httpClient.newTypedCall(
                    Request.Builder()
                            .method(request.method, request.payload)
                            .url(request.url)
                            .build(),
                    request.responseType)

            processCall(call)
        } catch (e: IOException) {
            didFail(e)
        } catch (e: JSONException) {
            didFail(e)
        }
    }

    private fun processCall(call: TypedCall) {
        val response = call.execute()
        if (response.body == null) {
            didFail(IOException("HTTP response have no body."))
            return
        }

        val stream = response.body!!.byteStream()
        when {
            response.code in 200..299 -> didSucceed(serializer.deserialize(stream, call.clazz))
            response.code in 300..399 -> didFail(RedirectionException())
            else -> didFail(serializer.deserialize(stream, APIError::class.java))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun didSucceed(model: Model) {
        replyHandler.post { listener.onRequestSucceed(model as T) }
    }

    private fun didFail(e: Throwable) {
        replyHandler.post {
            listener.onRequestFailed(e)
        }
    }
}

class TypedCall(
        private val call: Call,
        val clazz: Class<Model>
) {

    fun execute(): Response {
        return call.execute()
    }
}

@Suppress("UNCHECKED_CAST")
fun OkHttpClient.newTypedCall(okRequest: Request, clazz: Class<*>): TypedCall {
    return TypedCall(newCall(okRequest), clazz as Class<Model>)
}
