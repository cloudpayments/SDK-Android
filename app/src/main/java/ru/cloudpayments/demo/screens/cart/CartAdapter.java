package ru.cloudpayments.demo.screens.cart;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.cloudpayments.demo.base.BaseAdapter;
import ru.cloudpayments.demo.models.Product;

public class CartAdapter extends BaseAdapter<CartHolder> {

    private List<Product> items = new ArrayList<>();

    private OnClickListener listener;

    public void update(List<Product> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public CartHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CartHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(CartHolder holder, int position) {
        holder.bind(items.get(position));

        holder.itemView.setOnClickListener(v -> {
            if (listener == null) {
                return;
            }
            listener.onProductClick(items.get(position));
        });
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    interface OnClickListener {
        void onProductClick(Product item);
    }
}
