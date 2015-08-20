package com.client.omise;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import co.omise.*;

public class OmisePayment extends Activity {

    private Button mSubmit;
    private EditText mCardNo;
    private EditText mCardOwner;
    private EditText mCardCity;
    private EditText mPostcode;
    private EditText mSecure;
    private EditText mYear;
    private EditText mMonth;
    private TextView mRegister;
    private Context mContext;

    private KeyManager mManager;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        setContentView(R.layout.activity_omise_pay);


        mManager = new KeyManager(this);

        mContext = this;

        mSubmit = (Button) findViewById(R.id.button_submit);

        mCardNo = (EditText) findViewById(R.id.card_no);
        mCardOwner = (EditText) findViewById(R.id.card_name);
        mCardCity = (EditText) findViewById(R.id.city);
        mPostcode = (EditText) findViewById(R.id.postcode);
        mSecure = (EditText) findViewById(R.id.security);
        mYear = (EditText) findViewById(R.id.card_name);
        mMonth = (EditText) findViewById(R.id.card_name);

        mRegister = (TextView) findViewById(R.id.add_key);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToken();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SettingKey.class);
                startActivity(intent);
            }
        });
    }

    private void getToken() {
        String cardNo = mCardNo.getText().toString().trim();
        String cardName = mCardOwner.getText().toString().trim();
        String cardCity = mCardCity.getText().toString().trim();
        String cardPost = mPostcode.getText().toString().trim();
        String cardSecure = mSecure.getText().toString().trim();

        String  pubKey = mManager.getPublicKey();
        if (!pubKey.isEmpty()) {
            final Omise omise = new Omise();
            TokenRequest tokenRequest = new TokenRequest();
            tokenRequest.setPublicKey(pubKey);
            try {
                Card card = new Card();
                card.setName(cardName);
                card.setNumber(cardNo);
                card.setCity(cardCity);
                card.setPostalCode(cardPost);
                card.setExpirationMonth("12");
                card.setExpirationYear("2016");
                card.setSecurityCode(cardSecure);
                tokenRequest.setCard(card);
                omise.requestToken(tokenRequest, new RequestTokenCallback() {
                    @Override
                    public void onRequestSucceeded(final Token token) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(mContext, MainOmise.class);
                                intent.putExtra("massage", token.getId());
                                startActivity(intent);
                                finish();
                            }
                        });

                    }
                    @Override
                    public void onRequestFailed(final int errorCode) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                String message = getString(R.string.get_token_error);
                                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } catch (OmiseException e) {
                e.getMessage();
            }
        } else {
            String message = getString(R.string.get_pub_error);
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_omise_payment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
