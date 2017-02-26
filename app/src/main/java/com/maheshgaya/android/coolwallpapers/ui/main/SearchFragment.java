package com.maheshgaya.android.coolwallpapers.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.adapter.CategoryAdapter;
import com.maheshgaya.android.coolwallpapers.loader.CategoryAsyncTaskLoader;
import com.maheshgaya.android.coolwallpapers.data.Category;
import com.maheshgaya.android.coolwallpapers.ui.search.ExpandedSearchActivity;
import com.maheshgaya.android.coolwallpapers.util.FragmentUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/6/17.
 */

public class SearchFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<ArrayList<Category>> {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final int LOADER_ID = 1000;
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.recycle_view)RecyclerView mCategoryRecycleView;
    @BindView(R.id.search_layout)FrameLayout mSearchLayout;
    @BindView(R.id.search_toolbar_linearlayout)LinearLayout mSearchToolbarLinearLayout;

    //Search
    @BindView(R.id.search_appbarlayout)AppBarLayout mSearchAppbarLayout;
    @BindView(R.id.search_toolbar_edit_text)EditText mSearchEditText;

    private ArrayList<Category> mCategoryList;
    private CategoryAdapter mCategoryAdapter;

    public SearchFragment(){
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);
        mToolbar = FragmentUtils.getToolbar(getContext(), mToolbar, "");
        mCategoryList = new ArrayList<>();
        mCategoryAdapter = new CategoryAdapter(getContext(), mCategoryList);
        mCategoryRecycleView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mCategoryRecycleView.setLayoutManager(linearLayoutManager);
        mCategoryRecycleView.setAdapter(mCategoryAdapter);

        refreshContent();

        //initialize search toolbar
        mSearchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startExpandedSearch();
            }
        });
        mSearchToolbarLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startExpandedSearch();
            }
        });
        return rootView;
    }


    private void refreshContent(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void startExpandedSearch(){
        Intent expandedSearchIntent = new Intent(getActivity(), ExpandedSearchActivity.class);
        startActivity(expandedSearchIntent);
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
    public Loader<ArrayList<Category>> onCreateLoader(int id, Bundle args) {
        return new CategoryAsyncTaskLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Category>> loader, ArrayList<Category> data) {
        mCategoryList = new ArrayList<>(data);
        mCategoryAdapter.setList(mCategoryList);
    }


    @Override
    public void onLoaderReset(Loader<ArrayList<Category>> loader) {
        //does nothing
    }

}
