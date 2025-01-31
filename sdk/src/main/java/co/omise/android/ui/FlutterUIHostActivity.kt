package co.omise.android.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import co.omise.android.models.Serializer
import co.omise.android.models.Source
import co.omise.android.models.Token
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel

class FlutterUIHostActivity : FlutterActivity() {

    companion object {
        private const val CHANNEL_NAME = "omiseFlutterChannel"

        // Function to launch the Flutter Activity
        fun launchActivity(activityLauncher:  ActivityResultLauncher<Intent>,context:Context, methodName: String, arguments: Map<String, Any?>) {
            val intent = Intent(context, FlutterUIHostActivity::class.java)
            intent.putExtra("methodName", methodName)
            intent.putExtra("arguments", HashMap(arguments))
            activityLauncher.launch(intent)
        }
    }
    private inline fun <reified T : Parcelable> parseFromMap(data: Map<String, Any>?): T? {
        if (data == null) return null

        return try {
            val jsonString = Serializer().objectMapper().writeValueAsString(data) // Convert map to JSON
            val jsonInputStream = jsonString.byteInputStream() // Convert JSON string to byte stream

            // Deserialize into the reified type `T`
            Serializer().deserialize(jsonInputStream, T::class.java) // Deserialize into any Parcelable object
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the method name and arguments from the Intent
        val methodName = intent.getStringExtra("methodName")
        val arguments = intent.getSerializableExtra("arguments") as? HashMap<String, Any?>

        val flutterEngine = this.flutterEngine
        // Invoke the Flutter method via MethodChannel
        if (flutterEngine != null) {
        if (methodName != null && arguments != null) {
            flutterEngine.dartExecutor.let {
                MethodChannel(it, CHANNEL_NAME)
                    .invokeMethod(methodName, arguments, object : MethodChannel.Result {
                        override fun success(result: Any?) {
                            // The result here should not be used as its not the result form the api call after user completes the journey
                        }

                        override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
                            Log.e("Flutter ErrorHandler", "Error Code: $errorCode | Message: ${errorMessage ?: "No message"} | Details: ${errorDetails ?: "No details"}")
                        }

                        override fun notImplemented() {
                            throw UnsupportedOperationException("Method $methodName is not implemented yet.")
                        }
                    })
            }
        }
                MethodChannel(flutterEngine.dartExecutor, CHANNEL_NAME)
                    .setMethodCallHandler { call, result ->
                        if (call.method == "selectPaymentMethodResult") {
                            val resultData = call.arguments as? Map<*, *>
                            val token = parseFromMap<Token>(resultData?.get("token") as? Map<String, Any>)
                            val source = parseFromMap<Source>(resultData?.get("source") as? Map<String, Any>)

                            val intent = Intent().apply {
                                token?.let {
                                    putExtra(OmiseActivity.EXTRA_TOKEN, it.id)
                                    putExtra(OmiseActivity.EXTRA_TOKEN_OBJECT, it)
                                    putExtra(OmiseActivity.EXTRA_CARD_OBJECT, it.card)
                                }

                                source?.let {
                                    putExtra(OmiseActivity.EXTRA_SOURCE_OBJECT, it)
                                }
                            }
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            result.notImplemented()
                        }
                    }

        }
    }
}
