package co.omise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import co.omise.activity.MainActivity;
import android.util.Base64;

/**
 * tokenを取得するクラスです。
 */
public class Omise {
	
	/**
 	 * tokenをOmiseから取得します。
	 * @param tokenRequest
	 * @param callback
	 * @param connectTimeoutMillis コネクションが確立されるまでのタイムアウト(ms)
	 * @param readTimeoutMillis コネクションが確立されてから通信が完了するまでのタイムアウト(ms)
	 * @throws OmiseException
	 */
	public void requestToken(final TokenRequest tokenRequest, final RequestTokenCallback callback, final int connectTimeoutMillis, final int readTimeoutMillis) throws OmiseException{
		checkValidation(tokenRequest);

		new Thread(new Runnable() {
			public void run() {
				HttpsURLConnection sslconnection = null;
				BufferedReader br = null;
				
				try {
					URL url = new URL("https://vault.omise.co/tokens");
					
					sslconnection = (HttpsURLConnection)url.openConnection();
					sslconnection.setRequestMethod("POST");
					sslconnection.setUseCaches(false);

					//curl -u
					String userpass = tokenRequest.getPublicKey() + ":";
					String auth = Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
					sslconnection.setRequestProperty  ("Authorization", "Basic " + auth);
					
					
					//timeout
					sslconnection.setConnectTimeout(connectTimeoutMillis);
					sslconnection.setReadTimeout(readTimeoutMillis);
					
					sslconnection.setDoInput(true);
					sslconnection.setDoOutput(true);
					
					
					//put params
					StringBuilder paramSb = new StringBuilder();
					paramSb.append("card[name]="+tokenRequest.getCard().getName() + "&");
					paramSb.append("card[city]="+tokenRequest.getCard().getCity() + "&");
					paramSb.append("card[postal_code]="+tokenRequest.getCard().getPostalCode() + "&");
					paramSb.append("card[number]="+tokenRequest.getCard().getNumber() + "&");
					paramSb.append("card[expiration_month]="+tokenRequest.getCard().getExpirationMonth() + "&");
					paramSb.append("card[expiration_year]="+tokenRequest.getCard().getExpirationYear());
					
					PrintWriter printWriter = new PrintWriter(sslconnection.getOutputStream());
					printWriter.print(paramSb.toString());
					printWriter.close();
					if (sslconnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
						String buffer = null;
						StringBuffer sb = new StringBuffer();
				        br = new BufferedReader(new InputStreamReader(sslconnection.getInputStream()));
			            while((buffer = br.readLine()) != null){
			            	sb.append(buffer);
			            }

			            Token token = new JsonParser().parseTokenJson(sb.toString());
						callback.onRequestSucceeded(token);
						
						
						//test code 
						if (MainActivity.tvResponse != null) {
							final String json = sb.toString();
							MainActivity.HANDLER.post(new Runnable() {
								public void run() {
									try {
										MainActivity.tvResponse.setText(new JSONObject(json).toString(4));
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
						}
						
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
	
	/**
	 * tokenをOmiseから取得します。
	 * タイムアウトは接続確立までが10秒、確立されてから通信完了までが10秒です。
	 * @param tokenRequest
	 * @param callback
	 * @throws OmiseException
	 */
	public void requestToken(final TokenRequest tokenRequest, final RequestTokenCallback callback) throws OmiseException{
		requestToken(tokenRequest, callback, 10000, 10000);
	}
	
	private void checkValidation(final TokenRequest tokenRequest) throws OmiseException{
		if(tokenRequest.getPublicKey() == null){
			throw new OmiseException("public key is null.");
		}
		if(tokenRequest.getCard() == null) {
			throw new OmiseException("card is null.");
		}
	}
}
