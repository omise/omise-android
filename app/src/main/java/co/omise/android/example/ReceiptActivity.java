package co.omise.android.example;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import co.omise.android.models.Token;

public class ReceiptActivity extends BaseActivity {
    public static final String EXTRA_PRODUCT_ID = "ReceiptActivity.productId";
    public static final String EXTRA_TOKEN = "ReceiptActivity.token";

    private String productId() {
        return getIntent().getExtras().getString(EXTRA_PRODUCT_ID);
    }

    private Product product() {
        return repository().byId(productId());
    }

    private Token token() {
        return getIntent().getExtras().getParcelable(EXTRA_TOKEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        Product product = product();
        Token token = token();

        ImageView productImageView = (ImageView) findViewById(R.id.image_product);
        TextView productNameText = (TextView) findViewById(R.id.text_product_name);
        TextView acknowledgeText = (TextView) findViewById(R.id.text_acknowledge);

        Picasso.get().load(product.getImageUrl()).into(productImageView);
        productNameText.setText(product.getName());
        acknowledgeText.setText(String.format(getString(R.string.format_paid_with_card),
                product.formatPrice(this),
                token.card.lastDigits));
    }
}
