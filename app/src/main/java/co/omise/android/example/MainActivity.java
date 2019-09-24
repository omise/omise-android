package co.omise.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import co.omise.android.api.Client;
import co.omise.android.api.Request;
import co.omise.android.api.RequestListener;
import co.omise.android.models.Amount;
import co.omise.android.models.Capability;
import co.omise.android.models.Source;
import co.omise.android.ui.AuthorizingPaymentActivity;
import co.omise.android.ui.CreditCardActivity;
import co.omise.android.ui.OmiseActivity;
import co.omise.android.ui.PaymentCreatorActivity;

import static co.omise.android.AuthorizingPaymentURLVerifier.EXTRA_AUTHORIZED_URLSTRING;
import static co.omise.android.AuthorizingPaymentURLVerifier.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS;
import static co.omise.android.AuthorizingPaymentURLVerifier.EXTRA_RETURNED_URLSTRING;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ProductListAdapter listAdapter = null;

    private static String PUBLIC_KEY = "[PUBLIC_KEY]";

    private static int AUTHORIZING_PAYMENT_REQUEST_CODE = 0x3D5;
    private static int PAYMENT_CREATOR_REQUEST_CODE = 0x3D6;
    private static int CREDIT_CARD_REQUEST_CODE = 0x3D7;

    private EditText amountEdit;
    private EditText currencyEdit;
    private Button choosePaymentMethodButton;
    private Button creditCardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountEdit = findViewById(R.id.amount_edit);
        currencyEdit = findViewById(R.id.currency_edit);
        choosePaymentMethodButton = findViewById(R.id.choose_payment_method_button);
        creditCardButton = findViewById(R.id.credit_card_button);

        setTitle("Checkout");

        choosePaymentMethodButton.setOnClickListener(view -> choosePaymentMethod());
        creditCardButton.setOnClickListener(view -> payByCreditCard());

//        listAdapter = new ProductListAdapter(repository().all());

//        ListView productList = findViewById(R.id.list_products);
//        productList.setAdapter(listAdapter);
//        productList.setOnItemClickListener(this);
    }

    private void choosePaymentMethod() {
        double localAmount = Double.valueOf(amountEdit.getText().toString().trim());
        String currency = currencyEdit.getText().toString().trim().toLowerCase();
        Amount amount = Amount.fromLocalAmount(localAmount, currency);

        Client client = new Client(PUBLIC_KEY);
        Request<Capability> request = new Capability.GetCapabilitiesRequestBuilder().build();
        client.send(request, new RequestListener<Capability>() {
            @Override
            public void onRequestSucceed(@NotNull Capability model) {
                Intent intent = new Intent(MainActivity.this, PaymentCreatorActivity.class);
                intent.putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY);
                intent.putExtra(OmiseActivity.EXTRA_AMOUNT, amount.getAmount());
                intent.putExtra(OmiseActivity.EXTRA_CURRENCY, amount.getCurrency());
                intent.putExtra(OmiseActivity.EXTRA_CAPABILITY, model);
                startActivityForResult(intent, PAYMENT_CREATOR_REQUEST_CODE);
            }

            @Override
            public void onRequestFailed(@NotNull Throwable throwable) {

            }
        });
    }

    private void payByCreditCard() {
        Intent intent = new Intent(this, CreditCardActivity.class);
        intent.putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY);
        startActivityForResult(intent, CREDIT_CARD_REQUEST_CODE);
    }

    private void authorizeUrl() {
        Intent intent = new Intent(this, AuthorizingPaymentActivity.class);
        intent.putExtra(EXTRA_AUTHORIZED_URLSTRING, "https://pay.omise.co/offsites/");
        intent.putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, new String[]{"http://www.example.com"});
        startActivityForResult(intent, MainActivity.AUTHORIZING_PAYMENT_REQUEST_CODE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Product product = (Product) listAdapter.getItem(position);
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra(CheckoutActivity.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_authorizing_payment_action) {
            authorizeUrl();
            return true;
        } else if (item.getItemId() == R.id.menu_payment_creator_action) {
            choosePaymentMethod();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_LONG).show();
            return;
        }

        if (requestCode == AUTHORIZING_PAYMENT_REQUEST_CODE) {
            String url = data.getStringExtra(EXTRA_RETURNED_URLSTRING);
            Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
        } else if (requestCode == PAYMENT_CREATOR_REQUEST_CODE) {
            if (data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)) {
                Toast.makeText(getApplicationContext(), ((Source) data.getParcelableExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)).getId(), Toast.LENGTH_LONG).show();
            } else if (data.hasExtra(OmiseActivity.EXTRA_TOKEN)) {
                Toast.makeText(getApplicationContext(), data.getStringExtra(OmiseActivity.EXTRA_TOKEN), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CREDIT_CARD_REQUEST_CODE) {
            Toast.makeText(getApplicationContext(), data.getStringExtra(OmiseActivity.EXTRA_TOKEN), Toast.LENGTH_LONG).show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
