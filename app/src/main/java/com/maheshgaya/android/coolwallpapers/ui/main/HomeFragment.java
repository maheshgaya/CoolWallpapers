package com.maheshgaya.android.coolwallpapers.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.adapter.ImageAdapter;
import com.maheshgaya.android.coolwallpapers.data.Post;
import com.maheshgaya.android.coolwallpapers.ui.post.PostActivity;
import com.maheshgaya.android.coolwallpapers.util.UserAuthUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/6/17.
 */

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.toolbar_title)TextView mToolbarTitle;

    @BindView(R.id.recycle_view_home) RecyclerView mRecycleView;
    private ArrayList<Object> mImageUriList;
    private ImageAdapter mImageAdapter;
    private DatabaseReference mDatabaseReference;

    private ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                mImageUriList.add(new Post(
                        postSnapshot.child(Post.COLUMN_UID).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_TITLE).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_IMAGE_URL).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_DATE).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_DESCRIPTION).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_CATEGORY).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_TAGS).getValue().toString(),
                        postSnapshot.child(Post.COLUMN_LOCATION).getValue().toString()));
            }
            mImageAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

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
                Intent postIntent = new Intent(getActivity(), PostActivity.class);
                startActivity(postIntent); //TODO startActivityForResult(postIntent);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbarTitle.setText(getString(R.string.bottom_nav_home));

        mImageUriList = new ArrayList<>();
        mImageAdapter = new ImageAdapter(getContext(), mImageUriList);
        mRecycleView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.grid_item_columns));
        mRecycleView.setLayoutManager(gridLayoutManager);
        mRecycleView.setAdapter(mImageAdapter);
        mRecycleView.setItemViewCacheSize(20);
        mRecycleView.setDrawingCacheEnabled(true);
        mRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecycleView.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Post.TABLE_NAME);
        mDatabaseReference.addValueEventListener(mValueEventListener);

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mDatabaseReference.removeEventListener(mValueEventListener);
    }
}
