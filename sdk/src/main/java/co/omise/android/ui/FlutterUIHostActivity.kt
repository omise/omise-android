package co.omise.android.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel

class FlutterUIHostActivity : FlutterActivity() {

    companion object {
        private const val CHANNEL_NAME = "omiseFlutterChannel"

        // Function to launch the Flutter Activity
        fun launchActivity(context: Context, methodName: String, arguments: Map<String, Any?>) {
            val intent = Intent(context, FlutterUIHostActivity::class.java)
            intent.putExtra("methodName", methodName)
            intent.putExtra("arguments", HashMap(arguments))
            context.startActivity(intent)
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
                            // Handle successful invocation
                            println("This is result: $${result}")
                        }

                        override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
                            // Handle errors
                            println("Error:")
                            println(errorMessage)
                            println(errorDetails)
                            println(errorCode)
                        }

                        override fun notImplemented() {
                            // Handle method not implemented
                            println("Not implemented")
                        }
                    })
            }
        }
                MethodChannel(flutterEngine.dartExecutor, CHANNEL_NAME)
                    .setMethodCallHandler { call, result ->
                        println("Result from Flutter: $result")
                        if (call.method == "selectPaymentMethodResult") {
                            val resultData = call.arguments as? Map<*, *>
                            println("Parsed Result from Flutter: $resultData")

                        // TODO:
                        // Send response back to the originating activity, if needed
                        // For example, use setResult(RESULT_OK, intent) or any other handling
                        } else {
                            result.notImplemented()
                        }
                    }

        }
    }
}
