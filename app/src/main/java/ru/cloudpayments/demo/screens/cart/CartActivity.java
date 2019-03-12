package ru.cloudpayments.demo.screens.cart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import ru.cloudpayments.demo.R;
import ru.cloudpayments.demo.base.BaseListActivity;
import ru.cloudpayments.demo.managers.CartManager;
import ru.cloudpayments.demo.models.Product;
import ru.cloudpayments.demo.screens.checkout.CheckoutActivity;
import ru.cloudpayments.demo.support.SideSpaceItemDecoration;

public class CartActivity extends BaseListActivity<CartAdapter> implements CartAdapter.OnClickListener {

    @BindView(R.id.text_total)
    TextView textViewTotal;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cart;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setTitle(R.string.cart_title);

        initList();
        initTotal();
    }

    private void initList() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);

        recyclerView.addItemDecoration(new SideSpaceItemDecoration(this, 16,1, true));

        adapter = new CartAdapter();
        adapter.setHasStableIds(true);
        adapter.setListener(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.update(CartManager.getInstance().getProducts());
    }

    private void initTotal() {

        int total = 0;

        for (Product product : CartManager.getInstance().getProducts()) {
            total += Integer.parseInt(product.getPrice());
        }

        textViewTotal.setText(getString(R.string.cart_total) + " " + total + " " + getString(R.string.main_rub));
    }

    @OnClick(R.id.text_phone)
    void onPhoneClick() {
        String phone = getString(R.string.main_phone);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @OnClick(R.id.text_email)
    void onEmailClick() {
        String email = getString(R.string.main_email);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        startActivity(Intent.createChooser(emailIntent, getString(R.string.main_select_app)));
    }

    @Override
    public void onProductClick(Product item) {

    }

     @OnClick(R.id.button_go_to_payment)
    void onGoToPaymentClick() {
        startActivity(new Intent(this, CheckoutActivity.class));
     }
}
