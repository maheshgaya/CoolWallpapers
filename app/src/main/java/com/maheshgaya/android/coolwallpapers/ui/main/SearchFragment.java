package com.maheshgaya.android.coolwallpapers.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.adapter.CategoryAdapter;
import com.maheshgaya.android.coolwallpapers.adapter.CategoryAsyncTaskLoader;
import com.maheshgaya.android.coolwallpapers.adapter.ImageAdapter;
import com.maheshgaya.android.coolwallpapers.data.Category;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/6/17.
 */

public class SearchFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Category>> {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final int LOADER_ID = 1000;
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.toolbar_title)TextView mToolbarTitle;
    @BindView(R.id.recycle_view)RecyclerView mCategoryRecycleView;
    @BindView(R.id.swipe_refresh_layout)SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Category> mCategoryList;
    private CategoryAdapter mCategoryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        //TODO: if add menu remove this code for content insets
        mToolbar.setContentInsetsAbsolute(0, 0);
        mToolbar.setContentInsetsRelative(0, 0);
        mCategoryList = new ArrayList<>();
        mCategoryAdapter = new CategoryAdapter(getContext(), mCategoryList);
        mCategoryRecycleView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mCategoryRecycleView.setLayoutManager(linearLayoutManager);
        mCategoryRecycleView.setAdapter(mCategoryAdapter);
        refreshContent();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        return rootView;
    }

    private void refreshContent(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<List<Category>> onCreateLoader(int id, Bundle args) {
        return new CategoryAsyncTaskLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Category>> loader, List<Category> data) {
        mCategoryList = new ArrayList<>(data);
        mCategoryAdapter.setList(mCategoryList);
    }


    @Override
    public void onLoaderReset(Loader<List<Category>> loader) {
        //does nothing
    }
}
