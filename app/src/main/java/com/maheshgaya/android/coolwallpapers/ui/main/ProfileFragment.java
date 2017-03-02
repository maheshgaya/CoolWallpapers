package com.maheshgaya.android.coolwallpapers.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.adapter.ImageAdapter;
import com.maheshgaya.android.coolwallpapers.data.Post;
import com.maheshgaya.android.coolwallpapers.data.User;
import com.maheshgaya.android.coolwallpapers.ui.post.PostActivity;
import com.maheshgaya.android.coolwallpapers.ui.profile.ProfileEditActivity;
import com.maheshgaya.android.coolwallpapers.ui.profile.ProfileEditFragment;
import com.maheshgaya.android.coolwallpapers.util.UserAuthUtils;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/6/17.
 */

public class ProfileFragment extends Fragment{
    //Logging purposes
    private static final String TAG = ProfileFragment.class.getSimpleName();
    //Views
    /** Toolbar variables */
    @BindView(R.id.layout_profile)CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.profile_appbarlayout)AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar_title)TextView mToolbarTitle;
    @BindView(R.id.profile_name)TextView mProfileNameTextView;
    @BindView(R.id.profile_image_view)ImageView mProfileImageView;
    @BindView(R.id.edit_profile_button)Button mEditProfileButton;
    @BindView(R.id.follower_textview)TextView mFollowerTextView;
    @BindView(R.id.following_textview)TextView mFollowingTextView;
    @BindView(R.id.favorites_textview)TextView mLikesTextView;

    @BindView(R.id.profile_layout)LinearLayout mPostLinearLayout;
    @BindView(R.id.empty_linearlayout)LinearLayout mEmptyLayout;
    @BindView(R.id.empty_image_view)ImageView mEmptyImageView;
    @BindView(R.id.empty_text_view)TextView mEmptyTextView;
    @BindView(R.id.empty_action_text_view)TextView mEmptyActionTextView;


    /** gets the current user */
    private User mCurrentUser;

    @BindView(R.id.recycle_view)RecyclerView mRecycleView;
    private ArrayList<Object> mImageList;
    private ImageAdapter mImageAdapter;
    private FirebaseDatabase mDatabase;

    private ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (mImageList.size() != 0) {
                mImageList.clear();
            }
            if (dataSnapshot.getChildrenCount() > 0){
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
            Collections.reverse(mImageList);
            mImageAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    /**
     * enable toolbar menu buttons
     */
    public ProfileFragment(){
        setHasOptionsMenu(true);
    }

    /**
     * initializes the views
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        //get current user and display information
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (appBarLayout.getTotalScrollRange() + verticalOffset == 0){
                    if (mCurrentUser != null) {
                        mToolbarTitle.setText(mCurrentUser.getName());
                    }
                } else {
                    mToolbarTitle.setText(getActivity().getString(R.string.bottom_nav_profile));
                }
            }
        });

        //Add gridview and Initialize it
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
        mEmptyImageView.setImageResource(R.drawable.ic_big_post);
        mEmptyTextView.setText(getString(R.string.add_post_message));
        mEmptyActionTextView.setText(getString(R.string.action_post));
        mEmptyActionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPost();
            }
        });

        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentUser != null){
                    Intent profileIntent = new Intent(getActivity(), ProfileEditActivity.class);
                    profileIntent.putExtra(ProfileEditFragment.EDIT_EXTRA, mCurrentUser);
                    getActivity().startActivity(profileIntent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        refreshContent();
    }

    private void refreshContent(){
        DatabaseReference databaseReference = mDatabase.getReference(Post.TABLE_NAME);
        try {
            Query query = databaseReference.orderByChild(Post.COLUMN_UID).equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
            query.addValueEventListener(mValueEventListener);
        } catch (java.lang.NullPointerException e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDatabase = FirebaseDatabase.getInstance();
    }

    private void addPost(){
        Intent postIntent = new Intent(getActivity(), PostActivity.class);
        startActivity(postIntent);
    }

    private void showContent(boolean value){
        if (value){
            mEmptyLayout.setVisibility(View.GONE);
            mPostLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mPostLinearLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseUser user = UserAuthUtils.getCurrentUser();
        if (user != null) {
            //query database to see if user is there
            DatabaseReference userRef = mDatabase.getReference(User.TABLE_NAME);
            userRef.keepSynced(true);
            Query query = userRef.orderByKey().equalTo(user.getUid());

            //if user is present in database, return the record as a User object
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        String name = dataSnapshot.child(User.COLUMN_NAME).getValue().toString();
                        String email = dataSnapshot.child(User.COLUMN_EMAIL).getValue().toString();

                        mCurrentUser = new User(
                                dataSnapshot.child(User.COLUMN_UID).getValue().toString(),
                                (name.equals(""))? email: name, //handles bug with Firebase:10.0.1
                                email,
                                dataSnapshot.child(User.COLUMN_IMAGE_URL).getValue().toString(),
                                Integer.parseInt(dataSnapshot.child(User.COLUMN_FOLLOWERS).getValue().toString()),
                                Integer.parseInt(dataSnapshot.child(User.COLUMN_FOLLOWING).getValue().toString()),
                                Integer.parseInt(dataSnapshot.child(User.COLUMN_LIKES).getValue().toString())
                        );
                        mProfileNameTextView.setText(mCurrentUser.getName());
                        mFollowerTextView.setText(mCurrentUser.getFollowers() + " " +
                                ((mCurrentUser.getFollowers() > 1) ? getString(R.string.followers) : getString(R.string.follower)));
                        mFollowingTextView.setText(mCurrentUser.getFollowing()  + " " + getString(R.string.following));
                        mLikesTextView.setText(mCurrentUser.getLikes()  + " " +
                                ((mCurrentUser.getLikes() > 1) ? getString(R.string.favorites) : getString(R.string.favorite)));
                        Glide.with(getContext())
                                .load(mCurrentUser.getImageUrl())
                                .error(R.drawable.ic_user_profile)
                                .into(mProfileImageView);
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    /**
     *
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
                startActivity(postIntent);
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

}
