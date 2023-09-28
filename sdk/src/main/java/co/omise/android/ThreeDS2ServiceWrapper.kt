package co.omise.android

import android.app.Activity
import android.content.Context
import android.util.Log
import com.netcetera.threeds.sdk.api.ThreeDS2Service
import com.netcetera.threeds.sdk.api.configparameters.builder.ConfigurationBuilder
import com.netcetera.threeds.sdk.api.configparameters.builder.SchemeConfiguration
import com.netcetera.threeds.sdk.api.exceptions.InvalidInputException
import com.netcetera.threeds.sdk.api.exceptions.SDKAlreadyInitializedException
import com.netcetera.threeds.sdk.api.exceptions.SDKNotInitializedException
import com.netcetera.threeds.sdk.api.exceptions.SDKRuntimeException
import com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters
import com.netcetera.threeds.sdk.api.transaction.Transaction
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver
import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
import java.util.Collections


private const val TAG = "ThreeDS2ServiceWrapper"

class ThreeDS2ServiceWrapper(private val context: Context, private val threeDS2Service: ThreeDS2Service) {
    lateinit var transaction: Transaction
        private set

    fun initialize() {
        try {
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

            val locale = getLocale()
            val uiCustomization = UiCustomization()
            threeDS2Service.initialize(context, configParameters, locale, uiCustomization)
        } catch (e: Exception) {
            // Throw error if it not SDKAlreadyInitializedException
            if (e !is SDKAlreadyInitializedException) {
                throw e
            }
        }
    }

    private fun getLocale(): String {
        val defaultLocale = context.resources.configuration.locale
        val language = defaultLocale.language
        val country = defaultLocale.country
        return "$language-$country"
    }

    fun createTransaction(directoryServerId: String, messageVersion: String): Transaction {
        transaction = threeDS2Service.createTransaction(directoryServerId, messageVersion)
        return transaction
    }

    fun doChallenge(activity: Activity, challengeParameters: ChallengeParameters, receiver: ChallengeStatusReceiver, maxTimeout: Int) {
        transaction.doChallenge(activity, challengeParameters, receiver, maxTimeout)
    }
}
