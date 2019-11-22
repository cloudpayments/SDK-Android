package ru.cloudpayments.demo.screens.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.recyclerview.widget.GridLayoutManager;

import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.cloudpayments.demo.R;
import ru.cloudpayments.demo.api.ShopApi;
import ru.cloudpayments.demo.base.BaseListActivity;
import ru.cloudpayments.demo.managers.CartManager;
import ru.cloudpayments.demo.models.Product;
import ru.cloudpayments.demo.screens.cart.CartActivity;
import ru.cloudpayments.demo.support.SideSpaceItemDecoration;

public class MainActivity extends BaseListActivity<ProductsAdapter> implements ProductsAdapter.OnClickListener {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setTitle(R.string.main_title);

        initList();

        getProducts();
    }

    private void initList() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        recyclerView.addItemDecoration(new SideSpaceItemDecoration(this, 16,2, true));

        adapter = new ProductsAdapter();
        adapter.setHasStableIds(true);
        adapter.setListener(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart: {
                if (CartManager.getInstance().getProducts().isEmpty()) {
                    showToast(R.string.main_cart_is_empty);
                } else {
                    startActivity(new Intent(this, CartActivity.class));
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProductClick(Product item) {
        CartManager.getInstance().getProducts().add(item);
        showToast(R.string.main_product_added_to_cart);
    }

    private void getProducts() {
        compositeDisposable.add(ShopApi
                .getProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> showLoading())
                .doOnEach(notification -> hideLoading())
                .subscribe(products -> {
                    adapter.update(products);
                }, this::handleError));
    }


}
