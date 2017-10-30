package co.omise.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import co.omise.android.ui.CreditCardEditText;

public class TestCreditCardEditTextActivity extends AppCompatActivity {

    CreditCardEditText creditCardEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_credit_card_edit_text);

        creditCardEdit = findViewById(R.id.credit_card_edit);

    }
}
