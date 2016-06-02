package co.omise.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import co.omise.android.CreditCardActivity;

public class CheckoutActivity extends BaseActivity implements View.OnClickListener {
    public static final String OMISE_PKEY = "";
    public static final String PRODUCT_ID = "Product.id";
    public static final int CC_REQUEST = 100;

    private final ProductRepository repository = new ProductRepository();
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        product = repository.byId(getIntent().getExtras().getString(PRODUCT_ID));

        ImageView productImageView = (ImageView) findViewById(R.id.image_product);
        TextView productNameText = (TextView) findViewById(R.id.text_product_name);
        TextView productPriceText = (TextView) findViewById(R.id.text_product_price);
        Button checkoutButton = (Button) findViewById(R.id.button_checkout);

        Picasso.with(this).load(product.getImageUrl()).into(productImageView);
        productNameText.setText(product.getName());
        productPriceText.setText(product.getPriceString());
        checkoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, CreditCardActivity.class);
        startActivityForResult(intent, CC_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CC_REQUEST:
                if (resultCode == CreditCardActivity.OK) {
                    Toast.makeText(this, "done", Toast.LENGTH_LONG).show();
                } else if (resultCode == CreditCardActivity.CANCEL) {
                    Toast.makeText(this, "cancel", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
