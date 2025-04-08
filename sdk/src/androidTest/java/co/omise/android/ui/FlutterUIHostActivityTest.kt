package co.omise.android.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*

@RunWith(AndroidJUnit4::class)
class FlutterUIHostActivityTest {

    private lateinit var context: Context
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var flutterEngine: FlutterEngine
    private lateinit var dartExecutor: DartExecutor
    private lateinit var methodChannel: MethodChannel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        activityResultLauncher = mock<ActivityResultLauncher<Intent>>()
        flutterEngine = mock()
        dartExecutor = mock()
        methodChannel = mock()

    }

    @After
    fun tearDown() {
//        verifyNoMoreInteractions(
//            activityResultLauncher,
//            flutterEngine,
//            dartExecutor,
//            methodChannel
//        )
        reset(activityResultLauncher, flutterEngine, dartExecutor, methodChannel)
    }

    @Test
    fun launchActivity_createsIntentWithCorrectData() {
        val methodName = "testMethod"
        val arguments = mapOf("key1" to "value1", "key2" to 123)
        val intentCaptor = argumentCaptor<Intent>()

        FlutterUIHostActivity.launchActivity(activityResultLauncher, context, methodName, arguments)

        verify(activityResultLauncher).launch(intentCaptor.capture())

        val intent = intentCaptor.firstValue

        assertEquals(FlutterUIHostActivity::class.java.name, intent.component?.className)
        assertEquals(methodName, intent.getStringExtra("methodName"))
        assertEquals(HashMap(arguments), intent.getSerializableExtra("arguments"))
    }
    @Test
    fun activityLaunchesWithNoMock() {
        val methodName = "testMethod"
        val arguments = mapOf("key1" to "value1")
        val intent = Intent(context.applicationContext, FlutterUIHostActivity::class.java).apply {
            putExtra("methodName", methodName)
            putExtra("arguments", HashMap(arguments))
        }
        FlutterUIHostActivity.engineFlutter = null
        FlutterUIHostActivity.methodChannel = null
        ActivityScenario.launch<FlutterUIHostActivity>(intent)
    }
    @Test
    fun onCreate_invokesFlutterMethodOnInit() {
        val methodName = "testMethod"
        val arguments = mapOf("key1" to "value1")
        val intent = Intent(context.applicationContext, FlutterUIHostActivity::class.java).apply {
            putExtra("methodName", methodName)
            putExtra("arguments", HashMap(arguments))
        }
        FlutterUIHostActivity.engineFlutter = flutterEngine
        FlutterUIHostActivity.methodChannel = methodChannel
       ActivityScenario.launch<FlutterUIHostActivity>(intent)

        verify(methodChannel).invokeMethod(eq(methodName), eq(arguments), any())
        verify(methodChannel).setMethodCallHandler(any())
    }

    @Test
    fun onCreate_methodCallHandler_nullArguments_setsResultCancelledAndFinishes() {
        FlutterUIHostActivity.engineFlutter = flutterEngine
        FlutterUIHostActivity.methodChannel = methodChannel
        val intent = Intent(context.applicationContext, FlutterUIHostActivity::class.java)

        ActivityScenario.launch<FlutterUIHostActivity>(intent).onActivity { activity ->
            val methodCallHandlerCaptor = argumentCaptor<MethodChannel.MethodCallHandler>()
            verify(methodChannel).setMethodCallHandler(methodCallHandlerCaptor.capture())

            // Trigger the method call handler with null arguments
            val handler = methodCallHandlerCaptor.firstValue
            val mockResult = mock<MethodChannel.Result>()
            handler.onMethodCall(MethodCall("someMethod", null), mockResult)

            assertTrue(activity.isFinishing)
        }
    }
    @Test
    fun onCreate_methodCallHandler_validTokenResult_setsResultOkWithDataAndFinishes() {
        FlutterUIHostActivity.engineFlutter = flutterEngine
        FlutterUIHostActivity.methodChannel = methodChannel
        val intent = Intent(context.applicationContext, FlutterUIHostActivity::class.java)

        ActivityScenario.launch<FlutterUIHostActivity>(intent).onActivity { activity ->
            val methodCallHandlerCaptor = argumentCaptor<MethodChannel.MethodCallHandler>()
            verify(methodChannel).setMethodCallHandler(methodCallHandlerCaptor.capture())

            val tokenData: Map<String, Any> = mapOf(
                "object" to "token",
                "id" to "tok_123",
                "livemode" to true,
                "used" to false,
                "charge_status" to "pending",
                "card" to mapOf(
                    "object" to "card",
                    "id" to "card_123",
                    "livemode" to true,
                    "deleted" to false,
                    "brand" to "Visa",
                    "fingerprint" to "abcd1234",
                    "last_digits" to "4242",
                    "name" to "John Doe",
                    "expiration_month" to 12,
                    "expiration_year" to 2030,
                    "security_code_check" to true,
                    "created_at" to "2024-09-15T00:00:00Z"
                ),
                "created_at" to "2024-09-15T00:00:00Z"
            )
            val sourceData: Map<String, Any?> = mapOf(
                "object" to "source",
                "id" to "src_test_61ogzjw3to8279z8f6p",
                "livemode" to false,
                "location" to "/sources/src_test_61ogzjw3to8279z8f6p",
                "amount" to 20000,
                "barcode" to null,
                "bank" to null,
                "created_at" to "2024-11-08T08:34:14Z",
                "currency" to "THB",
                "email" to null,
                "flow" to "offline",
                "installment_term" to null,
                "ip" to "49.237.4.46",
                "absorption_type" to null,
                "name" to null,
                "mobile_number" to null,
                "phone_number" to null,
                "platform_type" to null,
                "scannable_code" to null,
                "billing" to null,
                "shipping" to null,
                "items" to emptyList<Any>(),
                "references" to null,
                "provider_references" to null,
                "store_id" to null,
                "store_name" to null,
                "terminal_id" to null,
                "type" to "promptpay",
                "zero_interest_installments" to null,
                "charge_status" to "unknown",
                "receipt_amount" to null,
                "discounts" to emptyList<Any>(),
                "promotion_code" to null
            )
            val resultData = mapOf("token" to tokenData,"source" to sourceData)
            val methodCall = MethodCall("onResult", resultData)
            val mockResult = mock<MethodChannel.Result>()
            val handler = methodCallHandlerCaptor.firstValue
            handler.onMethodCall(methodCall, mockResult)

            assertTrue(activity.isFinishing)
        }
    }
    @Test
    fun onCreate_methodCallHandler_validTokenResult_setsResultOkWithNullDataAndFinishes() {
        FlutterUIHostActivity.engineFlutter = flutterEngine
        FlutterUIHostActivity.methodChannel = methodChannel
        val intent = Intent(context.applicationContext, FlutterUIHostActivity::class.java)

        ActivityScenario.launch<FlutterUIHostActivity>(intent).onActivity { activity ->
            val methodCallHandlerCaptor = argumentCaptor<MethodChannel.MethodCallHandler>()
            verify(methodChannel).setMethodCallHandler(methodCallHandlerCaptor.capture())

            val tokenData = null
            val sourceData = null
            val resultData = mapOf("token" to tokenData,"source" to sourceData)
            val methodCall = MethodCall("onResult", resultData)
            val mockResult = mock<MethodChannel.Result>()
            val handler = methodCallHandlerCaptor.firstValue
            handler.onMethodCall(methodCall, mockResult)

            assertTrue(activity.isFinishing)
        }
    }
    @Test
    fun onCreate_invokesFlutterMethodOnInit_errorCallback_whenMock() {
        FlutterUIHostActivity.engineFlutter = flutterEngine
        FlutterUIHostActivity.methodChannel = methodChannel
        val methodName = "testMethodWithError"
        val arguments = mapOf("key1" to "value1")
        val intent = Intent(context.applicationContext, FlutterUIHostActivity::class.java).apply {
            putExtra("methodName", methodName)
            putExtra("arguments", HashMap(arguments))
        }

        val errorCode = "FLUTTER_ERROR"
        val errorMessage = "Something went wrong on Flutter side."
        val errorDetails = mapOf("detail" to "some extra info")

        // Set up the mock to call the error callback when invokeMethod is called
        `when`(methodChannel.invokeMethod(eq(methodName), eq(arguments), any()))
            .thenAnswer { invocation ->
                val resultCallback = invocation.getArgument<MethodChannel.Result>(2)
                resultCallback.error(errorCode, errorMessage, errorDetails)
                null
            }

        ActivityScenario.launch<FlutterUIHostActivity>(intent)

        // Verify that invokeMethod was called with the error
        verify(methodChannel).invokeMethod(eq(methodName), eq(arguments), any())
    }
}