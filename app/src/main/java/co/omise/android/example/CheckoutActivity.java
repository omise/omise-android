package co.omise.android.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import co.omise.android.models.Token;
import co.omise.android.ui.CreditCardActivity;

public class CheckoutActivity extends BaseActivity implements View.OnClickListener {
    public static final String OMISE_PKEY = "pkey_test_52d6po3fvio2w6tefpb";

    public static final String EXTRA_PRODUCT_ID = "CheckoutActivity.productId";
    public static final int REQUEST_CC = 100;

    private String productId() {
        return getIntent().getExtras().getString(EXTRA_PRODUCT_ID);
    }

    private Product product() {
        return repository().byId(productId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Product product = product();

        ImageView productImageView = (ImageView) findViewById(R.id.image_product);
        TextView productNameText = (TextView) findViewById(R.id.text_product_name);
        TextView productPriceText = (TextView) findViewById(R.id.text_product_price);
        Button checkoutButton = (Button) findViewById(R.id.button_checkout);

        Picasso.with(this).load(product.getImageUrl()).into(productImageView);
        productNameText.setText(product.getName());
        productPriceText.setText(product.formatPrice(this));
        checkoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, CreditCardActivity.class);
        intent.putExtra(CreditCardActivity.EXTRA_PKEY, OMISE_PKEY);
        startActivityForResult(intent, REQUEST_CC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CC:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }

                Token token = data.getParcelableExtra(CreditCardActivity.EXTRA_TOKEN_OBJECT);

                Intent intent = new Intent(this, ReceiptActivity.class);
                intent.putExtra(ReceiptActivity.EXTRA_PRODUCT_ID, productId());
                intent.putExtra(ReceiptActivity.EXTRA_TOKEN, token);
                startActivity(intent);
                finish();
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
