package ru.cloudpayments.demo.screens.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import ru.cloudpayments.demo.R;
import ru.cloudpayments.demo.base.BaseViewHolder;
import ru.cloudpayments.demo.models.Product;

public class ProductHolder extends BaseViewHolder {

    @BindView(R.id.image_product)
    ImageView imageViewProduct;

    @BindView(R.id.text_name)
    TextView textViewName;

    @BindView(R.id.text_price)
    TextView textViewPrice;

    public ProductHolder(View itemView) {
        super(itemView);
    }

    public void bind(Product item) {

        Context context = itemView.getContext();

        textViewName.setText(item.getName());
        textViewPrice.setText(item.getPrice() + " " + context.getString(R.string.main_rub));

        Glide
                .with(context)
                .load(item.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new CenterCrop()))
                .into(imageViewProduct);
    }

    public static ProductHolder create(ViewGroup parent) {
        return new ProductHolder(generateView(parent, R.layout.item_list_product));
    }
}
