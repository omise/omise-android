package co.omise.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

import co.omise.android.R;


// This is an experimental helper class in our SDK which would help you to handle 3DS verification process within your apps out of the box.
public class AuthorizingPaymentActivity extends Activity {
    public static final String EXTRA_AUTHORIZED_URLSTRING = "AuthorizingPaymentActivity.authorizedURL";
    public static final String EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS = "AuthorizingPaymentActivity.expectedReturnURLPatterns";
    public static final String EXTRA_RETURNED_URLSTRING = "AuthorizingPaymentActivity.returnedURL";

    private WebView webView;
    private AuthorizingPaymentURLVerifier verifier;

    private class AuthorizingPaymentURLVerifier {
        private Uri authorizedURL;
        private Uri[] expectedReturnURLPatterns;

        private AuthorizingPaymentURLVerifier(Uri authorizedURL, Uri[] expectedReturnURLPatterns) {
            this.authorizedURL = authorizedURL;
            this.expectedReturnURLPatterns = expectedReturnURLPatterns;
        }

        private AuthorizingPaymentURLVerifier(Intent intent) {
            authorizedURL = Uri.parse(intent.getStringExtra(EXTRA_AUTHORIZED_URLSTRING));
            String[] returnURLStringPatterns = intent.getStringArrayExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS);
            ArrayList<Uri> returnURLPatternList = new ArrayList<>(returnURLStringPatterns.length);
            for (String returnURLStringPattern : returnURLStringPatterns) {
                returnURLPatternList.add(Uri.parse(returnURLStringPattern));
            }

            expectedReturnURLPatterns = new Uri[returnURLPatternList.size()];
            expectedReturnURLPatterns = returnURLPatternList.toArray(expectedReturnURLPatterns);
        }

        public boolean isReady() {
            return getAuthorizedURL() != null
                    && getExpectedReturnURLPatterns() != null
                    && getExpectedReturnURLPatterns().length > 0;
        }

        public Uri getAuthorizedURL() {
            return authorizedURL;
        }

        public String getAuthorizedURLString() {
            if (authorizedURL == null) {
                return  null;
            }
            return authorizedURL.toString();
        }

        public Uri[] getExpectedReturnURLPatterns() {
            if (expectedReturnURLPatterns == null) {
                return  null;
            }
            return expectedReturnURLPatterns;
        }

        boolean verifyURL(Uri uri) {
            for (Uri expectedReturnURLPattern : getExpectedReturnURLPatterns()) {
                if (expectedReturnURLPattern.getScheme().equalsIgnoreCase(uri.getScheme()) &&
                        expectedReturnURLPattern.getHost().equalsIgnoreCase(uri.getHost()) &&
                        uri.getPath().startsWith(expectedReturnURLPattern.getPath())) {
                    return true;
                }
            }

            return false;
        }
    }

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
                } else {
                    return false;
                }
            }
        });
    }
}


