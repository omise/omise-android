package co.omise;

import android.util.Base64;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Class for get token
 */
public class Omise {
	
	private static final String OMISE_URL_TOKEN = "https://vault.omise.co/tokens";
	private static final String CHARSET = "UTF-8";
	private static final String _OMISE_API_VERSION = "2014-07-02";
	private static final String _OMISE_ANDROID_VERSION = "1.0.2";
	
	/**
	 * Get token from Omise 
	 * Timeout set 10 seccond until connection has completed、After 10 seccond connected API server will Timeout.
	 * @param tokenRequest
	 * @param callback
	 * @throws OmiseException
	 */
	public void requestToken(final TokenRequest tokenRequest, final RequestTokenCallback callback) throws OmiseException {
		requestToken(tokenRequest, callback, 10000, 10000);
	}

	/**
 	 * Get token from omise
	 * @param tokenRequest
	 * @param callback
	 * @param connectTimeoutMillis Connection timeout(ms)
	 * @param readTimeoutMillis Timeout for after communicate with server(ms)
	 * @throws OmiseException
	 */
	public void requestToken(final TokenRequest tokenRequest, final RequestTokenCallback callback, final int connectTimeoutMillis, final int readTimeoutMillis) throws OmiseException {
		checkValidation(tokenRequest);

		new Thread(new Runnable() {
			public void run() {
				HttpURLConnection sslconnection = null;
				BufferedReader br = null;

				try {
					URL url = new URL(OMISE_URL_TOKEN);

					//create HttpsURLConnection
					sslconnection = createHttpsURLConnection(url, tokenRequest.getPublicKey(), "", connectTimeoutMillis, readTimeoutMillis);

					//put params
					StringBuilder paramSb = new StringBuilder();
					paramSb.append("card[name]="+tokenRequest.getCard().getName() + "&");
					paramSb.append("card[city]="+tokenRequest.getCard().getCity() + "&");
					paramSb.append("card[postal_code]="+tokenRequest.getCard().getPostalCode() + "&");
					paramSb.append("card[number]="+tokenRequest.getCard().getNumber() + "&");
					paramSb.append("card[expiration_month]="+tokenRequest.getCard().getExpirationMonth() + "&");
					paramSb.append("card[expiration_year]="+tokenRequest.getCard().getExpirationYear() + "&");
					paramSb.append("card[security_code]=" + tokenRequest.getCard().getSecurityCode());
					PrintWriter printWriter = new PrintWriter(sslconnection.getOutputStream());
					printWriter.print(paramSb.toString());
					printWriter.close();
					sslconnection.connect();
					if (sslconnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						String buffer = null;
						StringBuffer sb = new StringBuffer();
				        br = new BufferedReader(new InputStreamReader(sslconnection.getInputStream()));
			            while((buffer = br.readLine()) != null){
			            	sb.append(buffer);
			            }

			            Token token = new JsonParser().parseTokenJson(sb.toString());
						callback.onRequestSucceeded(token);

					}else{
						callback.onRequestFailed(RequestTokenCallback.ERRCODE_BAD_REQUEST);
					}
				} catch (SocketTimeoutException e){
					e.printStackTrace();
					callback.onRequestFailed(RequestTokenCallback.ERRCODE_TIMEOUT);
				} catch (IOException e) {
					e.printStackTrace();
					callback.onRequestFailed(RequestTokenCallback.ERRCODE_CONNECTION_FAILED);
				} catch (JSONException e) {
					e.printStackTrace();
					callback.onRequestFailed(RequestTokenCallback.ERRCODE_INVALID_JSON);
				} catch (Exception e) {
					e.printStackTrace();
					callback.onRequestFailed(RequestTokenCallback.ERRCODE_UNKNOWN);
				} finally {
					if (sslconnection != null) sslconnection.disconnect();
					if (br != null){
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}){}.start();
	}


	private HttpURLConnection createHttpsURLConnection(
			final URL url,
			final String userName,
			final String password,
			final int connectTimeoutMillis,
			final int readTimeoutMillis) throws IOException{

		HttpURLConnection sslconnection = null;

		sslconnection = (HttpURLConnection)url.openConnection();
		sslconnection.setRequestMethod("POST");
		sslconnection.setUseCaches(false);

		//curl -u
		String userpass = userName.trim() + ":" + password;
		String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
		sslconnection.setRequestProperty ("Authorization", basicAuth);
		sslconnection.setRequestProperty ("User-Agent", "OmiseAndroid/" + _OMISE_ANDROID_VERSION + " OmiseAPI/" + _OMISE_API_VERSION);



		//timeout
		sslconnection.setConnectTimeout(connectTimeoutMillis);
		sslconnection.setReadTimeout(readTimeoutMillis);

		sslconnection.setDoInput(true);
		sslconnection.setDoOutput(true);

		return sslconnection;
	}

	private void checkValidation(final TokenRequest tokenRequest) throws OmiseException {
		if(!isSet(tokenRequest.getPublicKey())){
			throw new OmiseException("public key is required.");
		}
		if(tokenRequest.getCard() == null) {
			throw new OmiseException("card is null.");
		}
	}

	private boolean isSet(String str){
		return str != null && str.length() > 0;
	}
}


