package com.maheshgaya.android.coolwallpapers.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.adapter.CategoryAdapter;
import com.maheshgaya.android.coolwallpapers.sync.CategoryAsyncTaskLoader;
import com.maheshgaya.android.coolwallpapers.data.Category;
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
    @BindView(R.id.swipe_refresh_layout)SwipeRefreshLayout mSwipeRefreshLayout;

    //Search
    @BindView(R.id.search_appbarlayout)AppBarLayout mSearchAppbarLayout;
    @BindView(R.id.search_toolbar_edit_text)EditText mSearchEditText;
    @BindView(R.id.expanded_search_layout)LinearLayout mExpandedSearchLayout;
    @BindView(R.id.expanded_search_back_image_view)ImageView mExpandedSearchBackImageView;
    @BindView(R.id.expanded_search_cancel_button)ImageButton mExpandedSearchCancelImageButton;
    @BindView(R.id.expanded_search_edit_text)EditText mExpandedSearchEditText;

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

        showExpandedSearch(false);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mExpandedSearchBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExpandedSearch(false);
            }
        });

        mExpandedSearchCancelImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedSearchEditText.setText("");
            }
        });


        //initialize search toolbar
        mSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showExpandedSearch(true);
                }
            }
        });

        mExpandedSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() > 0){
                    mExpandedSearchCancelImageButton.setVisibility(View.VISIBLE);
                } else {
                    mExpandedSearchCancelImageButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootView;
    }

    private void showExpandedSearch(boolean value){
        //show expandedSearch
        if (value){
            mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
            mSearchAppbarLayout.setVisibility(View.INVISIBLE);
            mExpandedSearchLayout.setVisibility(View.VISIBLE);
            mExpandedSearchCancelImageButton.setVisibility(View.INVISIBLE);
        } else {
            mExpandedSearchEditText.setText("");
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getActivity().getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            try {
                inputMethodManager.hideSoftInputFromWindow(
                getActivity().getCurrentFocus().getWindowToken(), 0);
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            mExpandedSearchLayout.setVisibility(View.INVISIBLE);
            mSearchAppbarLayout.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
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
