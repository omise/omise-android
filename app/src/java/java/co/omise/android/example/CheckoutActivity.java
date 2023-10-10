package co.omise.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import co.omise.android.api.Client;
import co.omise.android.api.Request;
import co.omise.android.api.RequestListener;
import co.omise.android.config.ButtonCustomization;
import co.omise.android.config.ButtonCustomizationBuilder;
import co.omise.android.config.ButtonType;
import co.omise.android.config.LabelCustomization;
import co.omise.android.config.LabelCustomizationBuilder;
import co.omise.android.config.TextBoxCustomization;
import co.omise.android.config.TextBoxCustomizationBuilder;
import co.omise.android.config.ToolbarCustomization;
import co.omise.android.config.ToolbarCustomizationBuilder;
import co.omise.android.config.UiCustomization;
import co.omise.android.config.UiCustomizationBuilder;
import co.omise.android.models.Amount;
import co.omise.android.models.Capability;
import co.omise.android.models.Source;
import co.omise.android.models.Token;
import co.omise.android.ui.AuthorizingPaymentActivity;
import co.omise.android.ui.AuthorizingPaymentResult;
import co.omise.android.ui.CreditCardActivity;
import co.omise.android.ui.OmiseActivity;
import co.omise.android.ui.PaymentCreatorActivity;
import kotlin.Unit;
import kotlin.text.StringsKt;

import static co.omise.android.AuthorizingPaymentURLVerifier.EXTRA_AUTHORIZED_URLSTRING;
import static co.omise.android.AuthorizingPaymentURLVerifier.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS;
import static co.omise.android.ui.AuthorizingPaymentActivity.EXTRA_UI_CUSTOMIZATION;

public class CheckoutActivity extends AppCompatActivity {

    private static String TAG = "CheckoutActivity";
    private static String PUBLIC_KEY = "[PUBLIC_KEY]";
    private static String GOOGLEPAY_MERCHANT_ID = "[GOOGLEPAY_MERCHANT_ID]";
    private static boolean GOOGLEPAY_REQUEST_BILLING_ADDRESS = false;
    private static boolean GOOGLEPAY_REQUEST_PHONE_NUMBER = false;

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
        authorizeUrlButton.setOnClickListener(view -> AuthorizingPaymentDialog.showAuthorizingPaymentDialog(this, this::startAuthoringPaymentActivity));

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
        intent.putExtra(OmiseActivity.EXTRA_GOOGLEPAY_MERCHANT_ID, GOOGLEPAY_MERCHANT_ID);
        intent.putExtra(OmiseActivity.EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS, GOOGLEPAY_REQUEST_BILLING_ADDRESS);
        intent.putExtra(OmiseActivity.EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER, GOOGLEPAY_REQUEST_PHONE_NUMBER);

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

    private Unit startAuthoringPaymentActivity(String authorizeUrl, String returnUrl) {
        LabelCustomization labelCustomization = new LabelCustomizationBuilder()
                .headingDarkTextColor("#FFFFFF")
                .headingTextColor("#000000")
                .headingTextFontName("roboto_mono")
                .headingTextFontSize(20)
                .textFontName("roboto_mono")
                .textColor("#000000")
                .textFontSize(16)
                .build();

        TextBoxCustomization textBoxCustomization = new TextBoxCustomizationBuilder()
                .textFontName("font/roboto_mono_regular.ttf")
                .textColor("#000000")
                .textFontSize(16)
                .borderWidth(1)
                .cornerRadius(4)
                .borderColor("#1A56F0")
                .build();

        ToolbarCustomization toolbarCustomization = new ToolbarCustomizationBuilder()
                .textFontName("font/roboto_mono_bold.ttf")
                .textColor("#000000")
                .textFontSize(20)
                .backgroundColor("#FFFFFF")
                .headerText("Secure Checkout")
                .buttonText("Close")
                .darkBackgroundColor("#262626")
                .darkTextColor("#FFFFFF")
                .build();

        ButtonCustomization primaryButtonCustomization = new ButtonCustomizationBuilder()
                .textFontName("font/roboto_mono_bold.ttf")
                .textFontSize(20)
                .cornerRadius(4)
                .textColor("#FFFFFF")
                .backgroundColor("#1A56F0")
                .darkTextColor("#FFFFFF")
                .darkBackgroundColor("#4777F3")
                .build();

        ButtonCustomization secondaryButtonCustomization = new ButtonCustomizationBuilder()
                .textFontName("font/roboto_mono_bold.ttf")
                .textFontSize(20)
                .cornerRadius(4)
                .textColor("#1A56F0")
                .backgroundColor("#FFFFFF")
                .darkTextColor("#1E1E1E")
                .darkBackgroundColor("#FFFFFF")
                .build();

        UiCustomization uiCustomization = new UiCustomizationBuilder()
                .supportDarkMode(true)
                .labelCustomization(labelCustomization)
                .textBoxCustomization(textBoxCustomization)
                .toolbarCustomization(toolbarCustomization)
                .buttonCustomization(ButtonType.SUBMIT, primaryButtonCustomization)
                .buttonCustomization(ButtonType.CONTINUE, primaryButtonCustomization)
                .buttonCustomization(ButtonType.NEXT, primaryButtonCustomization)
                .buttonCustomization(ButtonType.OPEN_OOB_APP, primaryButtonCustomization)
                .buttonCustomization(ButtonType.RESEND, primaryButtonCustomization)
                .buttonCustomization(ButtonType.CANCEL, secondaryButtonCustomization)
                .build();

        Intent intent = new Intent(this, AuthorizingPaymentActivity.class);
        intent.putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeUrl);
        intent.putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, new String[]{returnUrl});
        intent.putExtra(EXTRA_UI_CUSTOMIZATION, uiCustomization);
        startActivityForResult(intent, CheckoutActivity.AUTHORIZING_PAYMENT_REQUEST_CODE);
        return Unit.INSTANCE;
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
            AuthorizingPaymentResult paymentResult = data.getParcelableExtra(AuthorizingPaymentActivity.EXTRA_AUTHORIZING_PAYMENT_RESULT);
            String resultMessage = null;
            if (paymentResult instanceof AuthorizingPaymentResult.ThreeDS1Completed) {
                resultMessage = "Authorization with 3D Secure version 1 completed: returnedUrl=" + ((AuthorizingPaymentResult.ThreeDS1Completed) paymentResult).getReturnedUrl();
            } else if (paymentResult instanceof AuthorizingPaymentResult.ThreeDS2Completed) {
                resultMessage = "Authorization with 3D Secure version 2 completed: transStatus=" + ((AuthorizingPaymentResult.ThreeDS2Completed) paymentResult).getTransStatus();
            } else if (paymentResult instanceof AuthorizingPaymentResult.Failure) {
                AuthorizingPaymentResult.Failure failure = (AuthorizingPaymentResult.Failure) paymentResult;
                resultMessage = failure.getThrowable().getMessage();
                failure.getThrowable().printStackTrace();
            } else if (paymentResult == null) {
                resultMessage = "Not found the authorization result.";
            }
            Log.d(TAG, resultMessage);
            snackbar.setText(resultMessage).show();
        } else if (requestCode == PAYMENT_CREATOR_REQUEST_CODE) {
            if (data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)) {
                Source source = data.getParcelableExtra(OmiseActivity.EXTRA_SOURCE_OBJECT);
                snackbar.setText(source.getId()).show();
                Log.d(TAG, "source: " + source.getId());
            } else if (data.hasExtra(OmiseActivity.EXTRA_TOKEN)) {
                Token token = data.getParcelableExtra(OmiseActivity.EXTRA_TOKEN_OBJECT);
                snackbar.setText(token.getId()).show();
                Log.d(TAG, "token: " + token.getId());
            }
        } else if (requestCode == CREDIT_CARD_REQUEST_CODE) {
            Token token = data.getParcelableExtra(OmiseActivity.EXTRA_TOKEN_OBJECT);
            snackbar.setText(token.getId()).show();
            Log.d(TAG, "token: " + token.getId());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
