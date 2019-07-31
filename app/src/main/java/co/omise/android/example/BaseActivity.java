package co.omise.android.example;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    private final ProductRepository repository = new ProductRepository();

    protected ProductRepository repository() {
        return repository;
    }
}
