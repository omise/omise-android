package co.omise.android.ui

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import co.omise.android.BuildConfig
import co.omise.android.R
import co.omise.android.config.AuthorizingPaymentConfig
import co.omise.android.threeds.ThreeDS
import co.omise.android.threeds.ThreeDSListener
import co.omise.android.threeds.core.ThreeDSConfig
import co.omise.android.threeds.events.CompletionEvent
import com.netcetera.threeds.sdk.ThreeDS2ServiceInstance
import com.netcetera.threeds.sdk.api.configparameters.builder.ConfigurationBuilder
import com.netcetera.threeds.sdk.api.configparameters.builder.SchemeConfiguration
import com.netcetera.threeds.sdk.api.exceptions.InvalidInputException
import com.netcetera.threeds.sdk.api.exceptions.SDKNotInitializedException
import com.netcetera.threeds.sdk.api.exceptions.SDKRuntimeException
import com.netcetera.threeds.sdk.api.transaction.Transaction
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver
import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.jetbrains.annotations.TestOnly
import org.json.JSONObject
import java.io.IOException
import java.util.Collections
import java.util.Locale


private const val TAG = "AuthorizingPaymentVM"

internal class AuthorizingPaymentViewModelFactory(private val activity: Activity) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        ThreeDSConfig.initialize(AuthorizingPaymentConfig.get().threeDSConfig.threeDSConfig)
        val threeDS = ThreeDS(activity)
        return AuthorizingPaymentViewModel(threeDS, activity.application) as T
    }
}

internal class AuthorizingPaymentViewModel(private val threeDS: ThreeDS, private val application: Application) :
    AndroidViewModel(application), ThreeDSListener {

    private val _authentication = MutableLiveData<AuthenticationResult>()
    val authentication: LiveData<AuthenticationResult> = _authentication

    private val _authenticationResponse = MutableLiveData<JSONObject>()
    val authenticationStatus: LiveData<String> = _authenticationResponse.map { it.getString("status") }

    private val _transactionStatus = MutableLiveData<String>()
    val transactionStatus: LiveData<String> = _transactionStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    lateinit var transaction: Transaction

    init {
        threeDS.setThreeDSListener(this)
    }

    fun initialize3DSTransaction() {
        try {
            val threeDS2Service = ThreeDS2ServiceInstance.get()

            // scheme from netcetera simulator
            val schemeConfig = SchemeConfiguration.newSchemeConfiguration(BuildConfig.SCHEME_NAME)
                .ids(Collections.singletonList(BuildConfig.DS_ID))
                .logo(R.drawable.logo_atome.toString())
                .logoDark(R.drawable.logo_atome.toString())
                .encryptionPublicKey(BuildConfig.DS_PUBLIC_KEY)
                .rootPublicKey(BuildConfig.DS_PUBLIC_KEY)
                .build()


            val configParameters = ConfigurationBuilder()
                .license(BuildConfig.NETCETERA_LICENSE_KEY)
                .configureScheme(schemeConfig)
                .build()

            val locale: String = Locale.getDefault().language
            val uiCustomization = UiCustomization()
            threeDS2Service.initialize(application, configParameters, locale, uiCustomization)

            val directoryServerID = BuildConfig.DS_ID
            val messageVersion = BuildConfig.MESSAGE_VERSION
            transaction = threeDS2Service.createTransaction(directoryServerID, messageVersion)
        } catch (e: Exception) {
            // TODO: fallback to authorize with webview
            Log.e(TAG, "Error initializing 3DS SDK", e)
            when (e) {
                is InvalidInputException -> Unit // TODO: handle error
                is SDKRuntimeException -> Unit // TODO: handle error
                is SDKNotInitializedException -> Unit // TODO: handle error
            }
        }
    }

    fun sendAuthenticationRequest(authorizeUrl: String) {
        try {
            _isLoading.postValue(true)
            val authenticationRequestParameters = transaction.authenticationRequestParameters
            Log.d(TAG, "authenticationRequestParameters.deviceData: ${authenticationRequestParameters.deviceData}")
            Log.d(TAG, "authenticationRequestParameters.sdkEphemeralPublicKey: ${authenticationRequestParameters.sdkEphemeralPublicKey}")
            Log.d(TAG, "authenticationRequestParameters.sdkReferenceNumber: ${authenticationRequestParameters.sdkReferenceNumber}")

            // Omise 3DS V2 - collect device info

            val body = """
                {
                  "areq": {
                    "sdkAppID": "${authenticationRequestParameters.sdkAppID}",
                    "sdkEphemPubKey": ${authenticationRequestParameters.sdkEphemeralPublicKey},
                    "sdkMaxTimeout": 5,
                    "sdkTransID": "${authenticationRequestParameters.sdkTransactionID}"
                  },
                  "device_info": {"DV":"1.6","DD":{"A006":"http:\/\/gsm.lge.com\/html\/gsm\/Nexus5-M3.xml","A007":"GoldfishNexus","A008":"us","A009":"310260","A010":"T-Mobile","A011":"10","A012":"1","A013":"1","A014":"us","A015":"310260","A016":"T-Mobile","A018":"5","A019":"Voicemail","A020":"+15557654321","A021":"true","A022":"false","A023":"false","A024":"true","A025":"true","A026":"true","A027":"false","A033":"false","A039":"02:00:00:00:00:00","A040":[],"A041":"true","A042":"goldfish_arm64","A043":"unknown","A044":"google","A045":"emu64a","A046":"TE1A.220922.012","A047":"google\/sdk_gphone64_arm64\/emu64a:13\/TE1A.220922.012\/9302419:user\/release-keys","A048":"ranchu","A049":"TE1A.220922.012","A050":"Google","A051":"sdk_gphone64_arm64","A052":"1.0.0.0","A054":[],"A055":["arm64-v8a"],"A056":"release-keys","A057":"1668654818000","A058":"user","A059":"android-build","A060":"REL","A061":"9302419","A062":"0","A063":"33","A064":"2022-11-05","A065":"false","A066":"false","A067":"true","A068":"http:\/\/www.google.com http:\/\/www.google.co.uk","A069":"76cc5517ef4e17fd","A070":"true","A071":"com.google.android.inputmethod.latin\/com.android.inputmethod.latin.LatinIME","A072":"true","A074":["com.google.android.inputmethod.latin","com.android.inputmethod.latin.LatinIME:com.google.android.tts","com.google.android.apps.speech.tts.googletts.settings.asr.voiceime.VoiceInputMethodService"],"A077":"true","A078":"false","A084":"true","A085":"cell,bluetooth,wifi,nfc,wimax","A086":"false","A087":"1","A088":"true","A089":"true","A090":"true","A093":"1","A094":"1","A095":"true","A097":"false","A099":"true","A103":"false","A104":"true","A105":"2","A106":"1","A107":"true","A108":"422","A109":"content:\/\/media\/internal\/audio\/media\/161?title=Pixie%20Dust&canonical=1","A110":"111","A111":"content:\/\/media\/internal\/audio\/media\/50?title=Flutey%20Phone&canonical=1","A112":"102","A113":"false","A114":"2147483647","A115":"true","A116":"false","A117":"false","A118":"false","A119":"false","A121":"0","A122":"false","A123":"false","A124":"false","A125":["android.ext.services.ExtServicesApplication","com.fime.emvco3ds.sdk.FimeApp","com.android.providers.media.MediaApplication","com.google.android.finsky.application.classic.ClassicApplication","com.android.nfc.NfcApplication","com.android.permissioncontroller.PermissionControllerApplication","com.google.android.setupwizard.SetupWizardApplication","com.android.se.SEApplication","com.google.android.apps.wellbeing.Wellbeing_Application","com.google.android.apps.docs.drive.DriveApplication","org.chromium.android_webview.nonembedded.WebViewApkApplication","org.chromium.chrome.browser.base.SplitChromeApplication","com.android.packageinstaller.PackageInstallerApplication","co.g.App","com.google.android.apps.speech.tts.googletts.GoogleTTSRoot_Application","com.android.managedprovisioning.ManagedProvisioningApplication","com.google.android.gms.common.app.GmsApplication","com.android.settings.SettingsApplication","com.android.phone.PhoneApp","com.android.systemui.SystemUIApplication","com.android.bluetooth.btservice.AdapterApp"],"A127":"88","A128":"19","A129":"mounted","A130":"789","A131":"2.75","A132":"440","A133":"2.75","A134":"72","A135":"72","A136":"6228115456","A137":"Mozilla\/5.0 (Linux; Android 13; sdk_gphone64_arm64 Build\/TE1A.220922.012; wv) AppleWebKit\/537.36 (KHTML, like Gecko) Version\/4.0 Chrome\/103.0.5060.71 Mobile Safari\/537.36","A138":"1","A139":"T-Mobile - US","A141":"1","A142":"T-Mobile - US","A143":"1","A145":"1","A146":"false","A149":[],"A150":"false","A151":"false","A152":"false","C001":"Android","C002":"Google||sdk_gphone64_arm64","C003":"Android TIRAMISU 13 API 33","C004":"13","C005":"en-US","C006":"420","C008":"1080x2214","C009":"sdk_gphone64_arm64","C010":"10.0.2.16","C013":"com.fime.emvco3ds.sdk.referenceapp","C014":"b8d20388-d177-4bf8-9d3b-ccf1f9cc56c0","C015":"1.0.0-alpha12","C016":"3DS_LOA_SDK_PPFU_020100_00007","C017":"20230830082957","C018":"4ae4af56-be8c-44fb-be06-4a99582307b7"},"DPNA":{"A001":"RE03","A002":"RE03","A003":"RE04","A004":"RE04","A005":"RE03","A017":"RE03","A028":"RE03","A029":"RE03","A030":"RE03","A031":"RE03","A032":"RE03","A034":"RE03","A035":"RE03","A036":"RE03","A037":"RE02","A038":"RE03","A053":"RE04","A073":"RE04","A075":"RE04","A076":"RE03","A079":"RE02","A080":"RE04","A081":"RE04","A082":"RE04","A083":"RE04","A091":"RE04","A092":"RE04","A096":"RE04","A098":"RE02","A100":"RE04","A101":"RE04","A102":"RE04","A120":"RE04","A126":"RE04","A140":"RE03","A147":"RE03","A148":"RE03","A153":"RE02","A154":"RE02","A155":"RE02","C011":"RE03","C012":"RE03"},"SW":["SW04"]},
                  "device_type": "Android"
                }
            """.trimIndent().toRequestBody("application/json".toMediaType())
            val authenticationRequest = Request.Builder()
                .url(authorizeUrl)
                .post(body)
                .build()

            val client = OkHttpClient();

            client.newCall(authenticationRequest).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    response.body?.string()?.let { JSONObject(it) }?.let {
                        Log.d(TAG, "${it.toString()}")
                        _authenticationResponse.postValue(it)
                        _isLoading.postValue(false)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Error sending authentication request", e)
                    _isLoading.postValue(false)
                }
            })
        } catch (e: SDKRuntimeException) {
            // TODO: fallback to authorize with webview
            Log.e(TAG, "Error sending authentication request", e)
        }
    }

    fun doChallenge(activity: Activity) {
        val json = _authenticationResponse.value ?: return
        val areqJson = json.getJSONObject("areq")
        val aresJson = json.getJSONObject("ares")
        val challengeParameters = ChallengeParameters().apply {
            set3DSServerTransactionID(aresJson.getString("threeDSServerTransID"))
            acsTransactionID = aresJson.getString("acsTransID")
            // TODO : check if where to get the sdkReferenceNumber value
//            acsRefNumber = areqJson.getString("sdkReferenceNumber")
//            acsRefNumber = viewModel.transaction.authenticationRequestParameters.sdkReferenceNumber
            acsRefNumber = BuildConfig.ACS_REF_NUMBER
            acsSignedContent = aresJson.getString("acsSignedContent")
        }

        Log.d(TAG, "challengeParameters.asRefNumber: ${challengeParameters.acsRefNumber}")
        Log.d(TAG, "challengeParameters.ascSignedContent: ${challengeParameters.acsSignedContent}")

        val receiver = object : ChallengeStatusReceiver {
            override fun completed(p0: com.netcetera.threeds.sdk.api.transaction.challenge.events.CompletionEvent?) {
                Log.d("AuthorizingPayment", "completed: $p0")
                _transactionStatus.value = p0?.transactionStatus
            }

            override fun cancelled() {
                Log.d("AuthorizingPayment", "cancelled")
            }

            override fun timedout() {
                Log.e("AuthorizingPayment", "timedout")
            }

            override fun protocolError(p0: com.netcetera.threeds.sdk.api.transaction.challenge.events.ProtocolErrorEvent?) {
                // TODO: fallback to authorize with webview?
                Log.e("AuthorizingPayment", "protocolError: $p0")
            }

            override fun runtimeError(p0: com.netcetera.threeds.sdk.api.transaction.challenge.events.RuntimeErrorEvent?) {
                // TODO: fallback to authorize with webview?
                Log.e("AuthorizingPayment", "runtimeError: $p0")
            }
        }

        try {
            transaction.doChallenge(activity, challengeParameters, receiver, 5)
        } catch (e: Exception) {
            Log.e("AuthorizingPayment", "Error to do challenge", e)
            when (e) {
                is InvalidInputException -> Unit
                is SDKRuntimeException -> Unit
            }
        }
    }

    @TestOnly
    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    private var coroutineScope: CoroutineScope = viewModelScope

    fun cleanup() {
        viewModelScope.cancel()
        threeDS.cleanup()
    }

    fun authorizeTransaction(authorizedUrl: String) {
        threeDS.authorizeTransaction(authorizedUrl)
    }

    override fun onCompleted(completionEvent: CompletionEvent) {
        _authentication.postValue(AuthenticationResult.AuthenticationCompleted(completionEvent))
    }

    override fun onFailure(e: Throwable) {
        _authentication.postValue(AuthenticationResult.AuthenticationFailure(e))
    }

    override fun onUnsupported() {
        _authentication.postValue(AuthenticationResult.AuthenticationUnsupported)
    }
}

sealed class AuthenticationResult {
    object AuthenticationUnsupported : AuthenticationResult()
    data class AuthenticationCompleted(val completionEvent: CompletionEvent) : AuthenticationResult()
    data class AuthenticationFailure(val error: Throwable) : AuthenticationResult()
}
