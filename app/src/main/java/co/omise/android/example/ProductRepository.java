package co.omise.android.example;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

public class ProductRepository {
    private Map<String, Product> products = new ImmutableMap.Builder<String, Product>()
            .put("coke", new Product("coke", "Coca-Cola", 29900, "http://vignette3.wikia.nocookie.net/logopedia/images/a/a8/Coca-Cola_1950.png/revision/latest?cb=20150801073948"))
            .put("pepsi", new Product("pepsi", "Pepsi", 29900, "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Pepsi_logo_2014.svg/2000px-Pepsi_logo_2014.svg.png"))
            .put("orangina", new Product("orangina", "Orangina", 39900, "http://vignette1.wikia.nocookie.net/logopedia/images/6/62/Orangina_logo.png/revision/latest?cb=20100323171215"))
            .build();

    public List<Product> all() {
        return ImmutableList.copyOf(products.values());
    }

    public Product byId(String id) {
        return products.get(id);
    }
}
