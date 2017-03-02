package com.maheshgaya.android.coolwallpapers.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.adapter.ImageAdapter;
import com.maheshgaya.android.coolwallpapers.data.Post;
import com.maheshgaya.android.coolwallpapers.ui.post.PostActivity;
import com.maheshgaya.android.coolwallpapers.util.UserAuthUtils;
import com.maheshgaya.android.coolwallpapers.util.Utils;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/6/17.
 */

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "position";
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.toolbar_title)TextView mToolbarTitle;
    @BindView(R.id.swipe_refresh_layout)SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycle_view) RecyclerView mRecycleView;
    @BindView(R.id.empty_linearlayout)LinearLayout mEmptyLayout;
    @BindView(R.id.empty_image_view)ImageView mEmptyImageView;
    @BindView(R.id.empty_text_view)TextView mEmptyTextView;
    @BindView(R.id.empty_action_text_view)TextView mEmptyActionTextView;

    private ArrayList<Object> mImageList;
    private ImageAdapter mImageAdapter;
    private DatabaseReference mDatabaseReference;
    private int mPosition = GridView.INVALID_POSITION;

    private ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (mImageList.size() != 0) {
                mImageList.clear();
            }
            if (dataSnapshot.getChildrenCount() > 0) {
                showContent(true);
            } else {
                showContent(false);
            }

            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                mImageList.add(new Post(
                        postSnapshot.child(Post.COLUMN_UID).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_TITLE).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_IMAGE_URL).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_DATE).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_DESCRIPTION).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_CATEGORY).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_TAGS).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_LOCATION).getValue().toString()));
            }
            //reverse chronological order, Firebase does not provide reverse order query
            Collections.reverse(mImageList);
            mImageAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mImageAdapter.getPosition() != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mImageAdapter.getPosition());
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * set options for fragment
     */
    public HomeFragment(){
        setHasOptionsMenu(true);
    }

    /**
     * adds menu for the fragment
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_add);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Open PostActivity
                addPost();
                return true;
            }
        });
        MenuItem signOutMenuItem = menu.findItem(R.id.action_sign_out);
        signOutMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                UserAuthUtils.signOut(getActivity());
                return true;
            }
        });
    }

    private void addPost(){
        Intent postIntent = new Intent(getActivity(), PostActivity.class);
        startActivity(postIntent);
    }

    private void showContent(boolean value){
        if (value){
            mEmptyLayout.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshContent();
    }

    private void refreshContent(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Post.TABLE_NAME);
        mDatabaseReference.keepSynced(true);
        Query postQuery = mDatabaseReference.orderByKey();
        postQuery.addValueEventListener(mValueEventListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbarTitle.setText(getString(R.string.bottom_nav_home));



        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        mImageList = new ArrayList<>();
        mImageAdapter = new ImageAdapter(getContext(), mImageList);
        mRecycleView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.grid_item_columns));
        mRecycleView.setLayoutManager(gridLayoutManager);
        mRecycleView.setAdapter(mImageAdapter);
        mRecycleView.setItemViewCacheSize(20);
        mRecycleView.setDrawingCacheEnabled(true);
        mRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecycleView.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);
        if (mPosition != GridView.INVALID_POSITION) {
            mRecycleView.smoothScrollToPosition(mPosition);
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent
        );

        mEmptyTextView.setText(getString(R.string.add_post_message));
        mEmptyImageView.setImageResource(R.drawable.ic_big_post);
        mEmptyActionTextView.setText(getString(R.string.action_post));
        mEmptyActionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPost();
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mDatabaseReference.removeEventListener(mValueEventListener);
    }
}
