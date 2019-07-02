package co.omise.android.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.ArrayList;
import co.omise.android.R;


/**
 * This is an experimental helper class in our SDK which would help you to handle 3DS verification process within your apps out of the box.
 * In case authorize with external app. By default open those external app when completed verification then sent result back our SDK.
 */
public class AuthorizingPaymentActivity extends Activity {
    public static final String EXTRA_AUTHORIZED_URLSTRING = "AuthorizingPaymentActivity.authorizedURL";
    public static final String EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS = "AuthorizingPaymentActivity.expectedReturnURLPatterns";
    public static final String EXTRA_RETURNED_URLSTRING = "AuthorizingPaymentActivity.returnedURL";

    private static final int REQUEST_EXTERNAL_CODE = 300;

    private WebView webView;
    private AuthorizingPaymentURLVerifier verifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorizing_payment);
        webView = (WebView) findViewById(R.id.authorizing_payment_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        setTitle(R.string.title_authorizing_payment);

        verifier = new AuthorizingPaymentURLVerifier(getIntent());
        if (verifier.isReady()) {
            webView.loadUrl(verifier.getAuthorizedURLString());
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (verifier.verifyURL(uri)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_RETURNED_URLSTRING, url);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    return true;
                } else if (verifier.verifyExternalURL(uri)) {
                    try {
                        Intent externalIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivityForResult(externalIntent, REQUEST_EXTERNAL_CODE);
                        return true;
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        return false;
                    }
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXTERNAL_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}

