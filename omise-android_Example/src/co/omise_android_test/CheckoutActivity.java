package co.omise_android_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class CheckoutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_checkout);
	}
	
	public void onCheckoutClick(View view){
		startActivity(new Intent(getApplicationContext(), ConnectOmiseActivity.class));
	}
}
