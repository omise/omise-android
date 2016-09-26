package co.omise.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.MalformedURLException;
import java.net.URL;

import co.omise.android.R;


// This is an experimental helper class in our SDK which would help you to handle 3DS verification process within your apps out of the box.
public class Verify3DSActivity extends Activity {
    public static final String EXTRA_AUTHORIZED_URL = "Verify3DSActivity.authorizedURL";
    public static final String EXTRA_REDIRECTED_URL = "Verify3DSActivity.redirectedURL";


    private WebView webView;
    private URL authorizedURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verfiy_3ds);
        webView = (WebView) findViewById(R.id.verify_3ds_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        setTitle(R.string.title_verify_3ds);

        Intent intent = getIntent();
        try {
            authorizedURL = new URL(intent.getStringExtra(EXTRA_AUTHORIZED_URL));
        } catch (MalformedURLException exception) {
            authorizedURL = null;
        }

        if (authorizedURL != null && authorizedURL.toString() != null) {
            webView.loadUrl(authorizedURL.toString());
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (verifyURL(view.getUrl()) && !url.equalsIgnoreCase(view.getUrl())) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_REDIRECTED_URL, url);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private Boolean verifyURL(String url) {
        try {
            URL currentURL = new URL(url);
            String[] paths = currentURL.getPath().split("/");
            return currentURL.getHost().startsWith("api") && currentURL.getHost().endsWith("omise.co") && paths.length >= 2 && (paths[0].equalsIgnoreCase("payments") || paths[1].equalsIgnoreCase("payments")) && paths[paths.length - 1].equalsIgnoreCase("complete");
        } catch (MalformedURLException exception) {
            return false;
        }
    }
}


