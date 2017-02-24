package com.maheshgaya.android.coolwallpapers.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    @BindView(R.id.search_toolbar_edit_text)EditText mSearchEditText;
    @BindView(R.id.search_toolbar_cancel_button)ImageButton mSearchCancel;
    @BindView(R.id.search_toolbar_image_view)ImageView mSearchImageView;
    @BindView(R.id.search_content_layout)FrameLayout mSearchContentLayout;
    @BindView(R.id.search_toolbar_framelayout)FrameLayout mSearchToolbarFrameLayout;

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
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        mSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEditText.setText("");
            }
        });

        mSearchContentLayout.setVisibility(View.GONE);
        mSearchCancel.setVisibility(View.INVISIBLE);

        mSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showContent(hasFocus);
            }
        });

        mSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchEditText.hasFocus()){
                    mSearchEditText.setText("");
                    showContent(false);
                } else {
                    mSearchEditText.findFocus();
                    showContent(true);
                }
            }
        });

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence query, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                showContent(true);

                if (query.length() > 0){
                    mSearchCancel.setVisibility(View.VISIBLE);
                } else {
                    mSearchCancel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootView;
    }

    private void showContent(boolean value){
        if (value) {
            mSearchContentLayout.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
            mSearchImageView.setImageResource(R.drawable.ic_back_dark);
        } else {
            mSearchContentLayout.setVisibility(View.INVISIBLE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            mSearchImageView.setImageResource(R.drawable.ic_search_dark);
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getActivity().getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            try {
                inputMethodManager.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(), 0);
            } catch (NullPointerException e){
                e.printStackTrace();
            }

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
