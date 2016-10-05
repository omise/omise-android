package co.omise.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOError;

import co.omise.android.CardIO;
import co.omise.android.CardNumber;
import co.omise.android.Client;
import co.omise.android.R;
import co.omise.android.TokenRequest;
import co.omise.android.TokenRequestListener;
import co.omise.android.models.APIError;
import co.omise.android.models.CardBrand;
import co.omise.android.models.Token;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class CreditCardActivity extends Activity {
    // input
    public static final String EXTRA_PKEY = "CreditCardActivity.publicKey";
    public static final int REQUEST_CODE_CARD_IO = 1000;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_credit_card, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(CardIO.isAvailable() && views.button(R.id.button_submit).isEnabled());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_card_io) {
            if (CardIO.isAvailable()) {
                Intent intent = CardIO.buildIntent(this);
                startActivityForResult(intent, REQUEST_CODE_CARD_IO);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CARD_IO) {
            if (data == null || !data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                return;
            }

            CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
            applyCardIOResult(scanResult);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                CardBrand brand = CardNumber.brand(pan);
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

            TextView textView = views.textView(R.id.text_error_message);
            textView.setVisibility(View.VISIBLE);

            String message = null;
            if (throwable instanceof IOError) {
                message = getString(R.string.error_io, throwable.getMessage());
            } else if (throwable instanceof APIError) {
                message = getString(R.string.error_api, ((APIError) throwable).message);
            } else {
                message = getString(R.string.error_unknown, throwable.getMessage());
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
        invalidateOptionsMenu();
    }

    private void applyCardIOResult(CreditCard data) {
        EditText numberField = views.editText(R.id.edit_card_number);
        EditText nameField = views.editText(R.id.edit_card_name);
        EditText securityCodeField = views.editText(R.id.edit_security_code);

        if (data.cardNumber != null && !data.cardNumber.isEmpty()) {
            numberField.setText(CardNumber.format(data.cardNumber));
        }

        if (data.cardholderName != null && !data.cardholderName.isEmpty()) {
            nameField.setText(data.cardholderName);
        }

        if (data.isExpiryValid()) {
            Spinner spinner = views.spinner(R.id.spinner_expiry_month);
            ExpiryMonthSpinnerAdapter monthAdapter = (ExpiryMonthSpinnerAdapter) spinner.getAdapter();
            spinner.setSelection(monthAdapter.getPosition(data.expiryMonth));

            spinner = views.spinner(R.id.spinner_expiry_year);
            ExpiryYearSpinnerAdapter yearAdapter = (ExpiryYearSpinnerAdapter) spinner.getAdapter() ;
            spinner.setSelection(yearAdapter.getPosition(data.expiryYear));
        }

        if (data.cvv != null && !data.cvv.isEmpty()) {
            securityCodeField.setText(data.cvv);
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (numberField.getText() == null || numberField.getText().toString().isEmpty()) {
            numberField.requestFocus();
            imm.showSoftInput(numberField, InputMethodManager.SHOW_IMPLICIT);
        } else if (nameField.getText() == null || nameField.getText().toString().isEmpty()) {
            nameField.requestFocus();
            imm.showSoftInput(nameField, InputMethodManager.SHOW_IMPLICIT);
        } else if (securityCodeField.getText() == null || securityCodeField.getText().toString().isEmpty()) {
            securityCodeField.requestFocus();
            imm.showSoftInput(securityCodeField, InputMethodManager.SHOW_IMPLICIT);
        }
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
        ActivityTokenRequestListener listener = new ActivityTokenRequestListener();
        try {
            new Client(pkey).send(tokenRequest, listener);
        } catch (Exception ex) {
            listener.onTokenRequestFailed(tokenRequest, ex);
        }

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
        if (!CardNumber.luhn(value)) {
            field.setError(String.format(getString(R.string.error_invalid), field.getHint()));
            return false;
        }

        return true;
    }
}
