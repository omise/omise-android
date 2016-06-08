package co.omise.android;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.omise.android.models.APIError;
import co.omise.android.models.Token;
import okhttp3.Call;
import okhttp3.CertificatePinner;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Client is the main entrypoint to the SDK. You can use the Client to send {@link TokenRequest}s.
 *
 * @see TokenRequest
 */
public class Client {
    private final String publicKey;
    private final OkHttpClient httpClient;

    private BackgroundThread background;

    /**
     * Creates a Client with the given public Key.
     *
     * @param publicKey The key with the {@code pkey_} prefix.
     */
    public Client(String publicKey) {
        this.background = new BackgroundThread();
        this.publicKey = publicKey;
        this.httpClient = buildHttpClient(publicKey);

        this.background.start();
    }

    /**
     * Sends the given request and invoke the callback on the listener.
     *
     * @param tokenRequest The request to send.
     * @param listener The listener to listen for request result.
     */
    public void send(final TokenRequest tokenRequest, final TokenRequestListener listener) {
        final Call call = httpClient.newCall(new Request.Builder()
                .url("https://vault.omise.co/tokens")
                .post(tokenRequest.buildFormBody())
                .build());

        background.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    if (response.body() == null) {
                        listener.onTokenRequestFailed(tokenRequest, new IOException("HTTP response have no body."));
                        return;
                    }

                    String rawJson = response.body().string();
                    if (200 <= response.code() && response.code() < 300) {
                        listener.onTokenRequestSucceed(tokenRequest, new Token(rawJson));
                    } else {
                        listener.onTokenRequestFailed(tokenRequest, new APIError(rawJson));
                    }

                } catch (IOException | JSONException e) {
                    listener.onTokenRequestFailed(tokenRequest, e);
                }
            }
        });
    }

    private OkHttpClient buildHttpClient(final String publicKey) {
        return new OkHttpClient.Builder()
                .certificatePinner(buildCertificatePinner())
                .addInterceptor(buildInterceptor())
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    private CertificatePinner buildCertificatePinner() {
        return new CertificatePinner.Builder()
                .add("vault.omise.co", "sha256/maqNsxEnwszR+xCmoGUiV636PvSM5zvBIBuupBn9AB8=")
                .build();
    }

    private Interceptor buildInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                return chain.proceed(chain.request()
                        .newBuilder()
                        .addHeader("User-Agent", buildUserAgent())
                        .addHeader("Authorization", Credentials.basic(publicKey, "x"))
                        .build());
            }
        };
    }

    private String buildUserAgent() {
        return "OmiseAndroid/" + Client.class.getPackage().getImplementationVersion() +
                " Java/" + System.getProperty("java.version");
    }
}
