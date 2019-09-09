package co.omise.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import co.omise.android.api.Client;
import co.omise.android.api.Request;
import co.omise.android.api.RequestListener;
import co.omise.android.models.Capability;
import co.omise.android.ui.AuthorizingPaymentActivity;
import co.omise.android.ui.PaymentCreatorActivity;

import static co.omise.android.AuthorizingPaymentURLVerifier.EXTRA_AUTHORIZED_URLSTRING;
import static co.omise.android.AuthorizingPaymentURLVerifier.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS;
import static co.omise.android.AuthorizingPaymentURLVerifier.EXTRA_RETURNED_URLSTRING;
import static co.omise.android.ui.PaymentCreatorActivity.EXTRA_AMOUNT;
import static co.omise.android.ui.PaymentCreatorActivity.EXTRA_CAPABILITY;
import static co.omise.android.ui.PaymentCreatorActivity.EXTRA_CURRENCY;
import static co.omise.android.ui.PaymentCreatorActivity.EXTRA_PKEY;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ProductListAdapter listAdapter = null;

    private static String PUBLIC_KEY = "[PUBLIC_KEY]";

    private static int AUTHORIZING_PAYMENT_REQUEST_CODE = 0x3D5;
    private static int PAYMENT_CREATOR_REQUEST_CODE = 0x3D6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listAdapter = new ProductListAdapter(repository().all());

        ListView productList = findViewById(R.id.list_products);
        productList.setAdapter(listAdapter);
        productList.setOnItemClickListener(this);
    }

    private void startPaymentCreatorActivity() {
        Client client = new Client(PUBLIC_KEY);
        Request<Capability> request = new Capability.GetCapabilitiesRequestBuilder().build();
        client.send(request, new RequestListener<Capability>() {
            @Override
            public void onRequestSucceed(@NotNull Capability model) {
                Intent intent = new Intent(MainActivity.this, PaymentCreatorActivity.class);
                intent.putExtra(EXTRA_PKEY, PUBLIC_KEY);
                intent.putExtra(EXTRA_AMOUNT, 50000);
                intent.putExtra(EXTRA_CURRENCY, "thb");
                intent.putExtra(EXTRA_CAPABILITY, model);
                startActivityForResult(intent, PAYMENT_CREATOR_REQUEST_CODE);
            }

            @Override
            public void onRequestFailed(@NotNull Throwable throwable) {

            }
        });
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
            Intent intent = new Intent(this, AuthorizingPaymentActivity.class);
            intent.putExtra(EXTRA_AUTHORIZED_URLSTRING, "https://pay.omise.co/offsites/");
            intent.putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, new String[]{"http://www.example.com"});
            startActivityForResult(intent, MainActivity.AUTHORIZING_PAYMENT_REQUEST_CODE);
            return true;
        } else if (item.getItemId() == R.id.menu_payment_creator_action) {
            startPaymentCreatorActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.AUTHORIZING_PAYMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            String url = data.getStringExtra(EXTRA_RETURNED_URLSTRING);
            Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
