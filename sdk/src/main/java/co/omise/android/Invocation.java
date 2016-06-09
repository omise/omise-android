package co.omise.android;

import android.os.Handler;

import org.json.JSONException;

import java.io.IOException;

import co.omise.android.models.APIError;
import co.omise.android.models.Token;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class Invocation {
    private final Handler replyHandler;
    private final OkHttpClient httpClient;
    private final TokenRequest request;
    private final TokenRequestListener listener;

    Invocation(Handler replyHandler, OkHttpClient httpClient, TokenRequest request, TokenRequestListener listener) {
        this.replyHandler = replyHandler;
        this.httpClient = httpClient;
        this.request = request;
        this.listener = listener;
    }

    public void invoke() {
        final Call call = httpClient.newCall(new Request.Builder()
                .url("https://vault.omise.co/tokens")
                .post(request.buildFormBody())
                .build());

        try {
            processCall(call);
        } catch (IOException | JSONException e) {
            didFail(e);
        }
    }

    private void processCall(Call call) throws IOException, JSONException {
        Response response = call.execute();
        if (response.body() == null) {
            didFail(new IOException("HTTP response have no body."));
            return;
        }

        String rawJson = response.body().string();
        if (200 <= response.code() && response.code() < 300) {
            didSucceed(new Token(rawJson));
        } else {
            didFail(new APIError(rawJson));
        }
    }

    private void didSucceed(final Token token) {
        replyHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onTokenRequestSucceed(request, token);
            }
        });
    }

    private void didFail(final Throwable e) {
        replyHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onTokenRequestFailed(request, e);
            }
        });
    }
}
