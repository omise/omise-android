package co.omise.android.example;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductListAdapter implements ListAdapter {
    private List<DataSetObserver> observers = Lists.newArrayList();
    private List<Product> products = ImmutableList.of();

    public ProductListAdapter(List<Product> products) {
        this.products = ImmutableList.copyOf(products);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_product, parent, false);
        }

        Product product = (Product) getItem(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_product);
        TextView productNameText = (TextView) convertView.findViewById(R.id.text_product_name);
        TextView productPriceText = (TextView) convertView.findViewById(R.id.text_product_price);

        Picasso.with(context).load(product.getImageUrl()).into(imageView);
        productNameText.setText(product.getName());
        productPriceText.setText(product.getPriceString());
        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        observers.remove(observer);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return products.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
