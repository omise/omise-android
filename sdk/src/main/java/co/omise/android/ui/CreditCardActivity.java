package co.omise.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOError;

import co.omise.android.Client;
import co.omise.android.PAN;
import co.omise.android.R;
import co.omise.android.TokenRequest;
import co.omise.android.TokenRequestListener;
import co.omise.android.models.APIError;
import co.omise.android.models.CardBrand;
import co.omise.android.models.Token;

public class CreditCardActivity extends Activity {
    // input
    public static final String EXTRA_PKEY = "CreditCardActivity.publicKey";

    // output
    public static final int RESULT_OK = 100;
    public static final int RESULT_CANCEL = 200;

    public static final String EXTRA_TOKEN = "CreditCardActivity.token";
    public static final String EXTRA_TOKEN_OBJECT = "CreditCardActivity.tokenObject";
    public static final String EXTRA_CARD_OBJECT = "CreditCardActivity.cardObject";

    private Views views = new Views(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);
        setTitle(R.string.default_form_title);

        views.spinner(R.id.spinner_expiry_month).setAdapter(new ExpiryMonthSpinnerAdapter());
        views.spinner(R.id.spinner_expiry_year).setAdapter(new ExpiryYearSpinnerAdapter());
        views.editText(R.id.edit_card_number).addTextChangedListener(new ActivityTextWatcher());
        views.button(R.id.button_submit).setOnClickListener(new ActivityOnClickListener());
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCEL);
        super.onBackPressed();
    }

    private class ActivityTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String pan = s.toString();
            if (pan.length() > 6) {
                CardBrand brand = PAN.brand(pan);
                if (brand != null && brand.getLogoResourceId() > -1) {
                    views.image(R.id.image_card_brand).setImageResource(brand.getLogoResourceId());
                    return;
                }
            }

            views.image(R.id.image_card_brand).setImageDrawable(null);
        }
    }

    private class ActivityOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.button_submit) {
                submit();
            }
        }
    }

    private class ActivityTokenRequestListener implements TokenRequestListener {
        @Override
        public void onTokenRequestSucceed(TokenRequest request, Token token) {
            Intent data = new Intent();
            data.putExtra(EXTRA_TOKEN, token.id);
            data.putExtra(EXTRA_TOKEN_OBJECT, token);
            data.putExtra(EXTRA_CARD_OBJECT, token.card);

            setResult(RESULT_OK, data);
            finish();
        }

        @Override
        public void onTokenRequestFailed(TokenRequest request, Throwable throwable) {
            enableForm();
            // TODO: Show error above the button.
            TextView textView = views.textView(R.id.text_error_message);
            textView.setVisibility(View.VISIBLE);

            String message = null;
            if (throwable instanceof IOError) {
                message = String.format(getString(R.string.error_api), throwable.getMessage());
            } else if (throwable instanceof APIError) {
                message = String.format(getString(R.string.error_api), ((APIError) throwable).message);
            } else {
                message = String.format(getString(R.string.error_unknown), throwable.getMessage());
            }

            textView.setText(message);
        }
    }

    private void disableForm() {
        setFormEnabled(false);
    }

    private void enableForm() {
        setFormEnabled(true);
    }

    private void setFormEnabled(boolean enabled) {
        views.editText(R.id.edit_card_number).setEnabled(enabled);
        views.editText(R.id.edit_card_name).setEnabled(enabled);
        views.editText(R.id.edit_security_code).setEnabled(enabled);
        views.spinner(R.id.spinner_expiry_month).setEnabled(enabled);
        views.spinner(R.id.spinner_expiry_year).setEnabled(enabled);
        views.button(R.id.button_submit).setEnabled(enabled);
    }

    private void submit() {
        EditText numberField = views.editText(R.id.edit_card_number);
        EditText nameField = views.editText(R.id.edit_card_name);
        EditText securityCodeField = views.editText(R.id.edit_security_code);

        boolean valid = validateNonEmpty(numberField) &
                validateNonEmpty(nameField) &
                validateNonEmpty(securityCodeField) &
                validateLuhn(numberField);
        if (!valid) {
            return;
        }

        int expiryMonth = (int) views.spinner(R.id.spinner_expiry_month).getSelectedItem();
        int expiryYear = (int) views.spinner(R.id.spinner_expiry_year).getSelectedItem();

        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.number = numberField.getText().toString();
        tokenRequest.name = nameField.getText().toString();
        tokenRequest.securityCode = securityCodeField.getText().toString();
        tokenRequest.expirationMonth = expiryMonth;
        tokenRequest.expirationYear = expiryYear;

        disableForm();

        String pkey = getIntent().getStringExtra(EXTRA_PKEY);
        new Client(pkey).send(tokenRequest, new ActivityTokenRequestListener());
    }

    private boolean validateNonEmpty(EditText field) {
        String value = field.getText().toString().trim();
        if (value.isEmpty()) {
            field.setError(String.format(getString(R.string.error_required), field.getHint()));
            return false;
        }

        return true;
    }

    private boolean validateLuhn(EditText field) {
        String value = field.getText().toString().trim();
        if (!PAN.luhn(value)) {
            field.setError(String.format(getString(R.string.error_invalid), field.getHint()));
            return false;
        }

        return true;
    }
}
