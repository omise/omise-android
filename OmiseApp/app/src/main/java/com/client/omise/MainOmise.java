package com.client.omise;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class MainOmise extends Activity {

    private TextView massageToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        massageToken = (TextView)findViewById(R.id.token);
        final String massage = getIntent().getStringExtra("massage");
        massageToken.setText(massage);
    }

}
