package co.omise.android;

import co.omise.android.models.Token;

/**
 * Listener for {@link TokenRequest} results.
 */
public interface TokenRequestListener {
    /**
     * Invoked when {@link TokenRequest} succeeds.
     *
     * @param request The original request.
     * @param token The token result.
     */
    void onTokenRequestSucceed(TokenRequest request, Token token);

    /**
     * Invoked when {@link TokenRequest} fails.
     *
     * Possible errors includes {@link org.json.JSONException} and general {@link java.io.IOException}
     *
     * @param request The original request.
     * @param throwable The error.
     */
    void onTokenRequestFailed(TokenRequest request, Throwable throwable);
}
