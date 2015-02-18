package co.omise.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import co.omise.Card;
import co.omise.Omise;
import co.omise.OmiseException;
import co.omise.R;
import co.omise.RequestTokenCallback;
import co.omise.Token;
import co.omise.TokenRequest;

public class MainActivity extends Activity {
	
	public static TextView tvResponse = null;
	public static Handler HANDLER = null;
	private EditText etPublicKey = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		HANDLER = new Handler();
		
		tvResponse = (TextView)findViewById(R.id.tvResponse);
		etPublicKey = (EditText)findViewById(R.id.etPublicKey);
		etPublicKey.setText("pkey_test_4ypsy7bkjqct74ov8y7");
		
		setTitle("omise-android sample");

		example();
	}

	
	public void onConnectClick(View view){
		example();
	}
	
	private void example(){
		tvResponse.setText("connecting...");
		try {
			
			Card card = new Card();
			card.setName("JOHN DOE");
			card.setCity("Bangkok");
			card.setPostalCode("10320");
			card.setNumber("4242424242424242");
			card.setExpirationMonth("11");
			card.setExpirationYear("2016");
			
			TokenRequest tokenRequest = new TokenRequest();
			tokenRequest.setPublicKey(etPublicKey.getText().toString());
			tokenRequest.setCard(card);
			
			Omise omise = new Omise();
			omise.requestToken(tokenRequest, new RequestTokenCallback() {
				@Override
				public void onRequestSucceeded(Token token) {
				}
				@Override
				public void onRequestFailed(final int errorCode) {
				}
			});
			
		} catch (OmiseException e) {
			e.printStackTrace();
		}
	}
}
