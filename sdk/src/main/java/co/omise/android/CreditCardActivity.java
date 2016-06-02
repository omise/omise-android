package co.omise.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

public class CreditCardActivity extends Activity implements TextWatcher {
    public static final int OK = 100;
    public static final int CANCEL = 200;

    private Views views = new Views(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);
        setTitle(R.string.default_form_title);

        views.spinner(R.id.spinner_expiry_month).setAdapter(new ExpiryMonthSpinnerAdapter());
        views.spinner(R.id.spinner_expiry_year).setAdapter(new ExpiryYearSpinnerAdapter());
        views.editText(R.id.edit_card_number).addTextChangedListener(this);
    }

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
