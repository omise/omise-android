package co.omise_android_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


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
		startActivity(new Intent(getApplicationContext(), CheckoutActivity.class));
	}

	private void checkAndTvSetIslandNum(){
		if (islandNum > 99)islandNum = 99;
		if (islandNum < 1)islandNum = 1;
		tvIslandNum.setText(String.valueOf(islandNum));
		tvPrice.setText("$ " + islandNum*2 + " m");
	}
    
}
