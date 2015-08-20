package com.client.omise;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingKey extends Activity {

    private EditText mPubKey;
    private Button mSaveKey;

    private Context mContext;
    private KeyManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_key);

        mManager = new KeyManager(this);
        mContext = this;

        mPubKey = (EditText) findViewById(R.id.public_key);
        mSaveKey = (Button) findViewById(R.id.button_save);

        mSaveKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mPubKey.getText().toString();
                if (!username.isEmpty()) {
                    boolean isSuccess = mManager.registerPubKey(username);
                    if (isSuccess) {
                        String message = getString(R.string.save_success);
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } else {
                    String message = getString(R.string.save_key_password_error);
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
