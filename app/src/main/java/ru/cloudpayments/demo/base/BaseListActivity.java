package ru.cloudpayments.demo.base;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import ru.cloudpayments.demo.R;

public abstract class BaseListActivity<T extends RecyclerView.Adapter>  extends BaseActivity{

    protected T adapter;

    // This fields are marked as public for ButterKnife normal binding
    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;

}
