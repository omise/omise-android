package co.omise.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private final ProductRepository repository = new ProductRepository();
    private ProductListAdapter listAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listAdapter = new ProductListAdapter(repository.all());

        ListView productList = (ListView) findViewById(R.id.list_products);
        productList.setAdapter(listAdapter);
        productList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Product product = (Product) listAdapter.getItem(position);
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra(CheckoutActivity.PRODUCT_ID, product.getId());
        startActivity(intent);
    }
}
