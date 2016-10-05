package co.omise.android;

import android.os.Build;
import android.os.Handler;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okhttp3.ConnectionSpec;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.TlsVersion;

/**
 * Client is the main entrypoint to the SDK. You can use the Client to send {@link TokenRequest}s.
 *
 * @see TokenRequest
 */
public class Client {
    private final String publicKey;
    private final OkHttpClient httpClient;
    private final Executor background;

    /**
     * Creates a Client with the given public Key.
     *
     * @param publicKey The key with the {@code pkey_} prefix.
     */
    public Client(String publicKey) throws GeneralSecurityException {
        this.publicKey = publicKey;
        this.httpClient = buildHttpClient(publicKey);
        this.background = Executors.newSingleThreadExecutor();
    }

    /**
     * Sends the given request and invoke the callback on the listener.
     *
     * @param request  The request to send.
     * @param listener The listener to listen for request result.
     */
    public void send(final TokenRequest request, final TokenRequestListener listener) {
        final Handler handler = new Handler();
        background.execute(new Runnable() {
            @Override
            public void run() {
                new Invocation(handler, httpClient, request, listener).invoke();
            }
        });
    }


    private OkHttpClient buildHttpClient(final String publicKey) throws GeneralSecurityException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1)
                .build();

        if (Build.VERSION.SDK_INT < 21) {
            X509TrustManager trustManager = TLSPatch.systemDefaultTrustManager();
            builder.sslSocketFactory(new TLSPatch.TLSSocketFactory(), trustManager);
        }

        return builder
                .certificatePinner(buildCertificatePinner())
                .addInterceptor(buildInterceptor())
                .connectionSpecs(Collections.singletonList(spec))
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
