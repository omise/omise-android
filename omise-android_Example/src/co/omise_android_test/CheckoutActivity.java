package co.omise_android_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class CheckoutActivity extends Activity {
	
	private TextView tvPrice = null;
	private TextView tvIslandNum = null;
	private TextView tvShippingMethod = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_checkout);
		
		tvPrice = (TextView)findViewById(R.id.tvPrice);
		tvIslandNum = (TextView)findViewById(R.id.tvIslandNum);
		tvShippingMethod = (TextView)findViewById(R.id.tvShippingMethod);
		
		final int islandNum = getIntent().getIntExtra("island", 1);
		tvPrice.setText(islandNum*2 + "m USD");
		tvIslandNum.setText(String.valueOf(islandNum));
		tvShippingMethod.setText(String.valueOf(islandNum));
	}
	
	public void onCheckoutClick(View view){
		ConnectOmiseActivity.activity = this;
		startActivity(new Intent(getApplicationContext(), ConnectOmiseActivity.class));
	}
}
