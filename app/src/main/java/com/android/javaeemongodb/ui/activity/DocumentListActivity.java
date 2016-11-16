package com.android.javaeemongodb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.android.javaeemongodb.R;
import com.android.javaeemongodb.data.model.MedicineModel;
import com.android.javaeemongodb.helper.IntentKeys;
import com.android.javaeemongodb.ui.activity.base.BaseNavDrawerActivity;
import com.android.javaeemongodb.ui.presenter.DocListPresenter;
import com.android.javaeemongodb.ui.presenter.implementation.DocumentListPresenterImpl;
import com.android.javaeemongodb.ui.view.DocumentListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentListActivity extends BaseNavDrawerActivity implements DocumentListView {
    private final String TAG = getClass().getSimpleName();
    private final int REQUEST_ADD_MODEL = 1;

    @BindView(R.id.root_view)
    public CoordinatorLayout rootView;

    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;

    @BindView(R.id.swipe_layout)
    public SwipeRefreshLayout refreshLayout;

    private DocListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_list);

        ButterKnife.bind(this);
        getDrawer();

        setupView();

        presenter = new DocumentListPresenterImpl(this);
        presenter.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_MODEL) {
            if (resultCode == RESULT_OK) {
                presenter.refreshDataSet();
                Snackbar.make(rootView, R.string.snack_bar_model_added, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setupView() {
        refreshLayout.setColorSchemeResources(
                R.color.colorPrimary);
    }

    @OnClick(R.id.fab_add)
    public void startAddModelActivity() {
        startActivityForResult(new Intent(DocumentListActivity.this, ModelAddActivity.class), REQUEST_ADD_MODEL);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public AppCompatActivity getActivity() {
        return this;
    }

    @Nullable
    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public boolean isRefreshing() {
        return refreshLayout.isRefreshing();
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        refreshLayout.setOnRefreshListener(onRefreshListener);
    }

    @Override
    public void showSnackBar(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void startModelInfoActivity(MedicineModel model) {
        startActivity(new Intent(DocumentListActivity.this, ModelInfoActivity.class)
                .putExtra(IntentKeys.OBJECT_MEDICINE_MODEL, model));
    }
}