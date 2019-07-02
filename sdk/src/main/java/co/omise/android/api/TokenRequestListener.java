package co.omise.android.api;

import co.omise.android.api.Request;
import co.omise.android.api.TokenRequest;
import co.omise.android.models.Token;

/**
 * Listener for {@link TokenRequest} results.
 */
public interface TokenRequestListener {
    /**
     * Invoked when {@link TokenRequest} succeeds.
     *
     * @param request The original request.
     * @param token   The token result.
     */
    void onTokenRequestSucceed(Request request, Token token);

    /**
     * Invoked when {@link TokenRequest} fails.
     * <p>
     * Possible errors includes {@link org.json.JSONException} and general {@link java.io.IOException}
     *
     * @param request   The original request.
     * @param throwable The error.
     */
    void onTokenRequestFailed(Request request, Throwable throwable);
}
