package co.omise_android_test;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;


public class ConnectOmiseActivity extends Activity {
	
	private TextView tvPrice = null;
	private TextView tvIslandNum = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_connect_omise);
    }
    
}
