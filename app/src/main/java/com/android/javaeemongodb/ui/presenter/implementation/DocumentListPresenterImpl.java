package com.android.javaeemongodb.ui.presenter.implementation;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.javaeemongodb.R;
import com.android.javaeemongodb.data.api.RetrofitAPI;
import com.android.javaeemongodb.data.model.ErrorModel;
import com.android.javaeemongodb.data.model.MedicineModel;
import com.android.javaeemongodb.helper.IntentKeys;
import com.android.javaeemongodb.ui.activity.ModelInfoActivity;
import com.android.javaeemongodb.ui.adapter.DocumentListAdapter;
import com.android.javaeemongodb.ui.dialog.bottomsheet.BottomSheetModelOptions;
import com.android.javaeemongodb.ui.presenter.DocListPresenter;
import com.android.javaeemongodb.ui.processor.DocListProcessor;
import com.android.javaeemongodb.ui.processor.implementation.DocListProcessorImpl;
import com.android.javaeemongodb.ui.view.DocumentListView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentListPresenterImpl implements DocListPresenter {
    private final String TAG = getClass().getSimpleName();

    private DocumentListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<MedicineModel> dataSet;
    private DocumentListView view;
    private DocListProcessor processor;

    public DocumentListPresenterImpl(@NonNull DocumentListView view) {
        this.view = view;
        this.processor = new DocListProcessorImpl(this);
    }

    @Override
    public void start() {
        dataSet = new ArrayList<>();
        layoutManager = new LinearLayoutManager(view.getContext());
        adapter = new DocumentListAdapter(view.getContext(), dataSet);

        setupAdapter(adapter);
        setupRecyclerView(view.getRecyclerView());
        setupProcessor();

        processor.fillDataSet(adapter.getDataSet());
        getAdapter().notifyDataSetChanged();

        getView().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getView().setRefreshing(true);
                processor.reloadDataSet(adapter.getDataSet());
            }
        });
    }

    @Override
    public void refreshDataSet() {
        processor.reloadDataSet(getAdapter().getDataSet());
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(getAdapter());
    }

    @Override
    public void setupAdapter(DocumentListAdapter adapter) {
        adapter.setOnItemClickListener(new DocumentListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MedicineModel model) {
                getView().startModelInfoActivity(model);
            }

            @Override
            public void onItemLongClick(MedicineModel model) {
                BottomSheetModelOptions dialog = new BottomSheetModelOptions();
                dialog.show(getView().getActivity().getSupportFragmentManager(), "BottomSheetModelOptions");
            }
        });

        adapter.setOnItemOptionsClickListener(new DocumentListAdapter.OnItemOptionsClickListener() {
            @Override
            public void onDelete(MedicineModel model) {
                deleteModel(model);
            }

            @Override
            public void onEdit(MedicineModel model) {
                getView().startModelInfoActivity(model);
            }
        });
    }

    @Override
    public void setupProcessor() {
        processor.setOnDataReloadListener(new DocListProcessorImpl.OnDataReloadListener() {
            @Override
            public void onLoaded() {
                getAdapter().notifyDataSetChanged();
                getView().setRefreshing(false);
            }
        });
    }

    @Override
    public DocumentListView getView() {
        return view;
    }

    @Override
    public DocumentListAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void deleteModel(final MedicineModel model) {
        RetrofitAPI.getInstance(getView().getContext()).deleteModel(model.getId()).enqueue(new Callback<ErrorModel>() {
            @Override
            public void onResponse(Call<ErrorModel> call, Response<ErrorModel> response) {
                if (response == null || response.body() == null || response.body().getError()) {
                    getView().showSnackBar(getView().getContext().getString(R.string.snack_bar_delete_failed));
                    return;
                }

                getView().showSnackBar(getView().getContext().getString(R.string.snack_bar_deleted));
                getAdapter().getDataSet().remove(model);
                getAdapter().notifyItemRemoved(adapter.getDataSet().indexOf(model));
            }

            @Override
            public void onFailure(Call<ErrorModel> call, Throwable t) {

            }
        });
    }
}