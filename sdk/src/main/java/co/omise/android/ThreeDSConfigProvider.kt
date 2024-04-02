import android.net.Uri
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.api.Client
import com.netcetera.threeds.sdk.api.exceptions.InvalidInputException
import java.net.URL

internal class ThreeDSConfigProvider(private val urlVerifier: AuthorizingPaymentURLVerifier, private val client: Client) {
    // Use the authorize_uri to create the config url
    fun createThreeDSConfigUrl(authUrl: String): String {
        try {
            // Check if the authUrl is valid
            URL(authUrl)
            val base = Uri.parse(authUrl).buildUpon().clearQuery().build()
            val parts = base.toString().split('/').toMutableList()
            parts[parts.lastIndex] = "config"
            val configEndPoint = parts.joinToString("/")
            // check that the generated url is valid
            URL(configEndPoint)
            return configEndPoint
        } catch (e: Exception) {
            throw InvalidInputException("Invalid URL: $authUrl", e)
        }
    }

    suspend fun getThreeDSConfigs(): NetceteraConfig {
        // create the config endpoint url
        val configUrl = createThreeDSConfigUrl(urlVerifier.authorizedURLString)
        // No body params so we just need to perform a GET on the config url
        val request = NetceteraConfig.NetceteraConfigRequestBuilder().configUrl(configUrl).build()
        // get the configuration from API
        return client.send(request)
    }
}
