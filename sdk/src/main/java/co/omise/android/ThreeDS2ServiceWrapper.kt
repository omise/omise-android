package co.omise.android

import NetceteraConfig
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Base64
import com.netcetera.threeds.sdk.api.ThreeDS2Service
import com.netcetera.threeds.sdk.api.configparameters.builder.ConfigurationBuilder
import com.netcetera.threeds.sdk.api.configparameters.builder.SchemeConfiguration
import com.netcetera.threeds.sdk.api.exceptions.SDKAlreadyInitializedException
import com.netcetera.threeds.sdk.api.transaction.Transaction
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver
import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
import java.security.MessageDigest
import java.util.Collections
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Encapsulates the [ThreeDS2Service] to allow for easier testing.
 */
internal class ThreeDS2ServiceWrapper(
    private val context: Context,
    private val threeDS2Service: ThreeDS2Service,
    private val uiCustomizationMap: Map<UiCustomization.UiCustomizationType, UiCustomization>,
) {
    lateinit var transaction: Transaction
        private set

    // Format the PEM certificate to be parsable by Netcetera
    private fun formatPemCertificate(input: String): String {
        return input.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "").replace("\r\n", "")
    }

    suspend fun initialize(netceteraConfig: NetceteraConfig) =
        suspendCoroutine<Result<Unit>> { continuation ->
            try {
                // Decrypt the Netcetera api key
                val encryptionKey = EncryptionUtils.hash512(netceteraConfig.directoryServerId!!).copyOf(32)
                val encryptedKey = Base64.decode(netceteraConfig.key, Base64.DEFAULT)
                val decryptedNetceteraApiKey = String(EncryptionUtils.aesDecrypt(encryptedKey, encryptionKey), Charsets.UTF_8)
                // Format the certificate
                val formattedCert = formatPemCertificate(netceteraConfig.deviceInfoEncryptionCertPem!!)
                // scheme from Netcetera simulator
                val schemeConfig =
                    SchemeConfiguration.newSchemeConfiguration(netceteraConfig.identifier)
                        .ids(Collections.singletonList(netceteraConfig.directoryServerId))
                        .logo(R.drawable.logo_atome.toString())
                        .logoDark(R.drawable.logo_atome.toString())
                        .encryptionPublicKey(formattedCert)
                        .rootPublicKey(formattedCert)
                        .build()
                val configParameters =
                    ConfigurationBuilder()
                        .apiKey(decryptedNetceteraApiKey)
                        .configureScheme(schemeConfig)
                        .build()
                val locale = getLocale()
                threeDS2Service.initialize(
                    context,
                    configParameters,
                    locale,
                    uiCustomizationMap,
                    object :
                        ThreeDS2Service.InitializationCallback {
                        override fun onCompleted() {
                            continuation.resume(Result.success(Unit))
                        }

                        override fun onError(throwable: Throwable) {
                            throw throwable
                        }
                    },
                )
            } catch (e: Exception) {
                if (e is SDKAlreadyInitializedException) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(e))
                }
            }
        }

    private fun getLocale(): String {
        val defaultLocale =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales[0]
            } else {
                @Suppress("DEPRECATION")
                context.resources.configuration.locale
            }
        val language = defaultLocale.language
        val country = defaultLocale.country
        return "$language-$country"
    }

    fun createTransaction(
        directoryServerId: String,
        messageVersion: String,
    ): Transaction {
        transaction = threeDS2Service.createTransaction(directoryServerId, messageVersion)
        return transaction
    }

    fun doChallenge(
        activity: Activity,
        challengeParameters: ChallengeParameters,
        receiver: ChallengeStatusReceiver,
        maxTimeout: Int,
    ) {
        transaction.doChallenge(activity, challengeParameters, receiver, maxTimeout)
    }
}
