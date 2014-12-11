package co.omise_android_test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import co.omise.Card;
import co.omise.Omise;
import co.omise.OmiseException;
import co.omise.RequestTokenCallback;
import co.omise.Token;
import co.omise.TokenRequest;


public class ConnectOmiseActivity extends Activity {
	
	public static CheckoutActivity activity = null;

	private Button btnConnect = null;
	private RequestTokenCallback requestTokenCallback = new RequestTokenCallback() {
		@Override
		public void onRequestSucceeded(final Token token) {
			runOnUiThread(new Runnable() {
				public void run() {
					btnConnect.setText(token.getId());
				}
			});
		}
		@Override
		public void onRequestFailed(final int errorCode) {
			runOnUiThread(new Runnable() {
				public void run() {
					btnConnect.setText("Sorry, Please try again.");
				}
			});
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_connect_omise);
        btnConnect = (Button)findViewById(R.id.btnConnect);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	requestToken();
    }
    

    public void onConnectButtonClick(View view) {
    	if (btnConnect.getText().toString().startsWith("tokn_")) {
		    android.text.ClipboardManager cm = (android.text.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		    cm.setText(btnConnect.getText().toString());
		    
		    Toast.makeText(this, "copied to clipboard", Toast.LENGTH_SHORT).show();
    		return;
		}
    	
    	requestToken();
	}
    
    public void onResetClick(View view){
    	if (activity != null)activity.finish();
    	finish();
    }
    
    private void requestToken(){
    	TokenRequest tokenRequest = new TokenRequest();
    	tokenRequest.setPublicKey("pkey_test_4ya4jkfg5s13s2lz6i8");
    	Card card = new Card();
    	card.setName("JOHN DOE");
        card.setCity("Bangkok"); 
        card.setPostalCode("10320");
        card.setNumber("4242424242424242");
        card.setExpirationMonth("11");
        card.setExpirationYear("2016");
        tokenRequest.setCard(card);
    	
    	Omise omise = new Omise();
    	try {
			omise.requestToken(tokenRequest, requestTokenCallback);
		} catch (OmiseException e) {
			btnConnect.setText("Sorry, Please try again.");
			e.printStackTrace();
		}
    }
}
