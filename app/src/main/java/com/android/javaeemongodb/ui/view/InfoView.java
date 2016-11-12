package com.android.javaeemongodb.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.android.javaeemongodb.data.model.MedicineModel;

public interface InfoView {
    Context getContext();

    @Nullable
    RecyclerView getRecyclerView();

    void setRefreshing(boolean refreshing);

    void setupView();

    boolean isRefreshing();

    void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener);

    MedicineModel getModel();

    MedicineModel getModelFromIntent(Intent intent);

    boolean checkIntent(Intent intent);
}