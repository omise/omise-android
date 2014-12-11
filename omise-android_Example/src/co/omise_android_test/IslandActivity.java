package co.omise_android_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import co.omise.Card;
import co.omise.Charge;
import co.omise.ChargeRequest;
import co.omise.Omise;
import co.omise.OmiseException;
import co.omise.RequestChargeCallback;
import co.omise.RequestTokenCallback;
import co.omise.Token;
import co.omise.TokenRequest;


public class IslandActivity extends Activity {
	
	private TextView tvPrice = null;
	private TextView tvIslandNum = null;

	private int islandNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_island);
        tvPrice = (TextView)findViewById(R.id.tvPrice);
        tvIslandNum = (TextView)findViewById(R.id.tvIslandNum);
        checkAndTvSetIslandNum();
    }
    
	public void onPlusClick(View view){
	    islandNum--;
	    checkAndTvSetIslandNum();
    }
	public void onMinusClick(View view){
		islandNum++;
		checkAndTvSetIslandNum();
	}
	
	public void onCheckoutClick(View view){
		Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
		intent.putExtra("island", islandNum);
		
		startActivity(intent);
	}

	private void checkAndTvSetIslandNum(){
		if (islandNum > 99)islandNum = 99;
		if (islandNum < 1)islandNum = 1;
		tvIslandNum.setText(String.valueOf(islandNum));
		tvPrice.setText("$ " + islandNum*2 + " m");
	}
	
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Omise omise = new Omise();
		try {
			Card card = new Card();
		    card.setName("JOHN DOE"); // Required
		    card.setCity("Bangkok"); // Required
		    card.setPostalCode("10320"); // Required
		    card.setNumber("4242424242424242"); // Required
		    card.setExpirationMonth("11"); // Required
		    card.setExpirationYear("2016"); // Required

		    // Instantiate new TokenRequest with public key and card.
		    TokenRequest tokenRequest = new TokenRequest();
		    tokenRequest.setPublicKey("pkey_test_4ya4w8z88182bq420wc"); // Required
		    tokenRequest.setCard(card);

		    // Requesting token.    
		    omise.requestToken(tokenRequest, new RequestTokenCallback() {
		        @Override
		        public void onRequestSucceeded(Token token) {
		        	System.out.println(token.getCreated());
		        	System.out.println(token.getId());
		        	
		    		ChargeRequest chargeRequest = new ChargeRequest("skey_test_4ya4w8z87btooqkue6p");
		    		chargeRequest.setCustomer("");
		    		chargeRequest.setDescription("order9999");
		    		chargeRequest.setAmount(123456);
		    		chargeRequest.setCurrency("thb");
		    		chargeRequest.setReturnUri("http://www.example.com/orders/9999/complete");
		    		chargeRequest.setCard(token.getId());
					try {
						new Omise().requestCharge(chargeRequest, new RequestChargeCallback() {
							
							@Override
							public void onRequestSucceeded(Charge charge) {
								System.out.println(charge.getCard().getCity());
							}
							
							@Override
							public void onRequestFailed(int errorCode) {
								System.out.println("err" + errorCode);
							}
						});
					} catch (OmiseException e) {
						e.printStackTrace();
					}
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
