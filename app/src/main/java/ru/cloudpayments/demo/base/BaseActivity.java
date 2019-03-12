package ru.cloudpayments.demo.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import ru.cloudpayments.demo.R;
import ru.cloudpayments.demo.api.events.EmptyEvent;
import ru.cloudpayments.demo.api.response.PayApiError;

public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = "TAG_" + getClass().getSimpleName().toUpperCase();

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    protected int toolbarTitleId;

    private MaterialDialog loadingDialog;

    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initLoadingDialog();
    }

    private void initLoadingDialog() {
        loadingDialog = new MaterialDialog
                .Builder(this)
                .progress(true, 0)
                .title(R.string.dialog_loading_title)
                .content(R.string.dialog_loading_content)
                .cancelable(false)
                .build();
    }

    @Nullable
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }*/

    public void showLoading() {
        if (loadingDialog.isShowing()) {
            return;
        }

        loadingDialog.show();
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);

        toolbarTitleId = titleId;
    }

    public void hideLoading() {
        if (!loadingDialog.isShowing()) {
            return;
        }

        loadingDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void onNothing(EmptyEvent event) {
    }

    public void showToast(@StringRes int resId) {
        showToast(getString(resId));
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void log(String message) {
        Log.d(TAG, message);
    }

    public void handleError(Throwable throwable, Class... ignoreClasses) {

        if (ignoreClasses.length > 0) {
            List<Class> classList = Arrays.asList(ignoreClasses);

            if (classList.contains(throwable.getClass())) {
                return;
            }
        }

        if (throwable instanceof PayApiError) {
            PayApiError apiError = (PayApiError) throwable;

            String message = apiError.getMessage();
            showToast(message);
        } else if (throwable instanceof UnknownHostException) {
            showToast(R.string.common_no_internet_connection);
        } else {
            showToast(throwable.getMessage());
        }
    }
}
