package co.omise.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import co.omise.android.api.Client;
import co.omise.android.api.Request;
import co.omise.android.api.RequestListener;
import co.omise.android.config.AuthorizingPaymentConfig;
import co.omise.android.config.ThreeDSConfig;
import co.omise.android.config.UiCustomization;
import co.omise.android.models.Amount;
import co.omise.android.models.Capability;
import co.omise.android.models.Source;
import co.omise.android.models.Token;
import co.omise.android.ui.AuthorizingPaymentActivity;
import co.omise.android.ui.CreditCardActivity;
import co.omise.android.ui.OmiseActivity;
import co.omise.android.ui.PaymentCreatorActivity;
import kotlin.text.StringsKt;

import static co.omise.android.AuthorizingPaymentURLVerifier.EXTRA_AUTHORIZED_URLSTRING;
import static co.omise.android.AuthorizingPaymentURLVerifier.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS;
import static co.omise.android.AuthorizingPaymentURLVerifier.EXTRA_RETURNED_URLSTRING;

public class CheckoutActivity extends AppCompatActivity {

    private static String PUBLIC_KEY = "[PUBLIC_KEY]";
    private static String TOKEN_ID = "[TOKEN_ID]";

    private static int AUTHORIZING_PAYMENT_REQUEST_CODE = 0x3D5;
    private static int PAYMENT_CREATOR_REQUEST_CODE = 0x3D6;
    private static int CREDIT_CARD_REQUEST_CODE = 0x3D7;

    private EditText amountEdit;
    private EditText currencyEdit;
    private Button choosePaymentMethodButton;
    private Button creditCardButton;
    private Button authorizeUrlButton;
    private Snackbar snackbar;

    private Capability capability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initializeAuthoringPaymentConfig();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.activity_checkout);
        }

        amountEdit = findViewById(R.id.amount_edit);
        currencyEdit = findViewById(R.id.currency_edit);
        choosePaymentMethodButton = findViewById(R.id.choose_payment_method_button);
        creditCardButton = findViewById(R.id.credit_card_button);
        authorizeUrlButton = findViewById(R.id.authorize_url_button);
        snackbar = Snackbar.make(findViewById(R.id.content), "", Snackbar.LENGTH_SHORT);

        choosePaymentMethodButton.setOnClickListener(view -> choosePaymentMethod());
        creditCardButton.setOnClickListener(view -> payByCreditCard());
        authorizeUrlButton.setOnClickListener(view -> authorizeUrl());

        Client client = new Client(PUBLIC_KEY);
        Request<Capability> request = new Capability.GetCapabilitiesRequestBuilder().build();
        client.send(request, new RequestListener<Capability>() {
            @Override
            public void onRequestSucceed(@NotNull Capability model) {
                capability = model;
            }

            @Override
            public void onRequestFailed(@NotNull Throwable throwable) {
                snackbar.setText(StringsKt.capitalize(throwable.getMessage())).show();
            }
        });
    }

    private void choosePaymentMethod() {
        boolean isUsedSpecificsPaymentMethods = PaymentSetting.isUsedSpecificsPaymentMethods(this);

        if (!isUsedSpecificsPaymentMethods && capability == null) {
            snackbar.setText(R.string.error_capability_have_not_set_yet);
            return;
        }

        double localAmount = Double.valueOf(amountEdit.getText().toString().trim());
        String currency = currencyEdit.getText().toString().trim().toLowerCase();
        Amount amount = Amount.fromLocalAmount(localAmount, currency);

        Intent intent = new Intent(CheckoutActivity.this, PaymentCreatorActivity.class);
        intent.putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY);
        intent.putExtra(OmiseActivity.EXTRA_AMOUNT, amount.getAmount());
        intent.putExtra(OmiseActivity.EXTRA_CURRENCY, amount.getCurrency());

        if (isUsedSpecificsPaymentMethods) {
            intent.putExtra(OmiseActivity.EXTRA_CAPABILITY, PaymentSetting.createCapabilityFromPreferences(this));
        } else {
            intent.putExtra(OmiseActivity.EXTRA_CAPABILITY, capability);
        }

        startActivityForResult(intent, PAYMENT_CREATOR_REQUEST_CODE);
    }

    private void payByCreditCard() {
        Intent intent = new Intent(this, CreditCardActivity.class);
        intent.putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY);
        startActivityForResult(intent, CREDIT_CARD_REQUEST_CODE);
    }

    private void initializeAuthoringPaymentConfig() {
        UiCustomization uiCustomization = new UiCustomization.Builder()
                .labelCustomization(new UiCustomization.LabelCustomization.Builder()
                        .textFontName("RobotoMono-Regular.ttf")
                        .textFontColor("#000000")
                        .textFontSize(16)
                        .headingTextColor("#000000")
                        .headingTextFontName("RobotoMono-Bold.ttf")
                        .headingTextFontSize(20)
                        .build())
                .textBoxCustomization(new UiCustomization.TextBoxCustomization.Builder()
                        .textFontName("RobotoMono-Regular.ttf")
                        .textFontColor("#000000")
                        .textFontSize(16)
                        .borderWidth(2)
                        .cornerRadius(8)
                        .borderColor("#FF0000")
                        .build())
                .toolbarCustomization(new UiCustomization.ToolbarCustomization.Builder()
                        .textFontName("RobotoMono-Bold.ttf")
                        .textFontColor("#000000")
                        .textFontSize(20)
                        .backgroundColor("#FFFFFF")
                        .headerText("Secure Checkout")
                        .buttonText("Close")
                        .build())
                .buttonCustomization(UiCustomization.ButtonType.SUBMIT_BUTTON, new UiCustomization.ButtonCustomization.Builder()
                        .textFontName("RobotoMono-Bold.ttf")
                        .textFontColor("#FFFFFF")
                        .textFontSize(20)
                        .backgroundColor("#FF0000")
                        .cornerRadius(8)
                        .build())
                .buttonCustomization(UiCustomization.ButtonType.RESEND_BUTTON, new UiCustomization.ButtonCustomization.Builder()
                        .textFontName("RobotoMono-Bold.ttf")
                        .textFontColor("#000000")
                        .textFontSize(20)
                        .backgroundColor("#FFFFFF")
                        .cornerRadius(8)
                        .build())
                .build();

        ThreeDSConfig threeDSConfig = new ThreeDSConfig.Builder()
                .uiCustomization(uiCustomization)
                .timeout(5)
                .build();
        AuthorizingPaymentConfig authPaymentConfig = new AuthorizingPaymentConfig.Builder()
                .threeDSConfig(threeDSConfig)
                .build();
        AuthorizingPaymentConfig.initialize(authPaymentConfig);
    }

    private void authorizeUrl() {
        Intent intent = new Intent(this, AuthorizingPaymentActivity.class);
        intent.putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY);
        intent.putExtra(OmiseActivity.EXTRA_TOKEN, TOKEN_ID);
        intent.putExtra(EXTRA_AUTHORIZED_URLSTRING, "https://pay.omise.co/offsites/");
        intent.putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, new String[]{"http://www.example.com"});
        startActivityForResult(intent, CheckoutActivity.AUTHORIZING_PAYMENT_REQUEST_CODE);
    }

    private void openPaymentSetting() {
        Intent intent = new Intent(this, PaymentSettingActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_setup) {
            openPaymentSetting();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            snackbar.setText(R.string.payment_cancelled).show();
            return;
        }

        if (requestCode == AUTHORIZING_PAYMENT_REQUEST_CODE) {
            String url = data.getStringExtra(EXTRA_RETURNED_URLSTRING);
            snackbar.setText(url).show();
        } else if (requestCode == PAYMENT_CREATOR_REQUEST_CODE) {
            if (data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)) {
                Source source = data.getParcelableExtra(OmiseActivity.EXTRA_SOURCE_OBJECT);
                snackbar.setText(source.getId()).show();
            } else if (data.hasExtra(OmiseActivity.EXTRA_TOKEN)) {
                Token token = data.getParcelableExtra(OmiseActivity.EXTRA_TOKEN_OBJECT);
                snackbar.setText(token.getId()).show();
            }
        } else if (requestCode == CREDIT_CARD_REQUEST_CODE) {
            Token token = data.getParcelableExtra(OmiseActivity.EXTRA_TOKEN_OBJECT);
            snackbar.setText(token.getId()).show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
