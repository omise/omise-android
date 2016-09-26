package co.omise.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import co.omise.android.ui.Verify3DSActivity;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ProductListAdapter listAdapter = null;

    private static int VERIFY_3DS_REQUEST_CODE = 0x3D5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listAdapter = new ProductListAdapter(repository().all());

        ListView productList = (ListView) findViewById(R.id.list_products);
        productList.setAdapter(listAdapter);
        productList.setOnItemClickListener(this);
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
        if (item.getItemId() == R.id.menu_3ds_verify_action) {
            Intent intent = new Intent(this, Verify3DSActivity.class);
            intent.putExtra(Verify3DSActivity.EXTRA_AUTHORIZED_URL, "https://api.omise.co/payments/paym_12345/authorize");
            startActivityForResult(intent, MainActivity.VERIFY_3DS_REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.VERIFY_3DS_REQUEST_CODE && resultCode == RESULT_OK) {
            String url = data.getStringExtra(Verify3DSActivity.EXTRA_REDIRECTED_URL);
            Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
