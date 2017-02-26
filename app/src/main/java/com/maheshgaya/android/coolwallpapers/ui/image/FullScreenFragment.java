package com.maheshgaya.android.coolwallpapers.ui.image;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maheshgaya.android.coolwallpapers.Constants;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.Post;
import com.maheshgaya.android.coolwallpapers.data.User;
import com.maheshgaya.android.coolwallpapers.service.SetWallpaperIntentService;
import com.maheshgaya.android.coolwallpapers.util.DateUtils;
import com.maheshgaya.android.coolwallpapers.util.DisplayUtils;
import com.maheshgaya.android.coolwallpapers.util.FragmentUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/21/17.
 */

public class FullScreenFragment extends Fragment {
    private static final String TAG = FullScreenActivity.class.getSimpleName();
    public static final int FULL_IMAGE_CODE = 1205;
    public static final String IMAGE_DELETE_KEY = "image_deleted";
    @BindView(R.id.full_screen_image_view)ImageView mImageView;
    @BindView(R.id.full_screen_toolbar)Toolbar mToolbar;
    @BindView(R.id.layout_full_screen_image)CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.full_screen_appbarlayout)AppBarLayout mAppBarLayout;
    @BindView(R.id.fs_detail_linear_layout)LinearLayout mDetailLinearLayout;
    @BindView(R.id.fs_favorite_imagebutton)ImageButton mFavoriteButton;

    @BindView(R.id.fs_detail_category)TextView mCategoryTextView;
    @BindView(R.id.fs_detail_date)TextView mDateTextView;
    @BindView(R.id.fs_detail_description)TextView mDescriptionTextView;
    @BindView(R.id.fs_detail_location)TextView mLocationTextView;
    @BindView(R.id.fs_detail_title)TextView mTitleTextView;
    @BindView(R.id.fs_detail_submitter)TextView mSubmitterTextView;
    @BindView(R.id.fs_detail_tags)TextView mTagsTextView;

    @BindView(R.id.fs_image_view)ImageView mProfileImageView;

    private GestureDetector mGestureDetector;
    private Bitmap mSelectedBitmap;
    public static final String POST_EXTRA = "post_extra";
    private Post mPost;
    private boolean showDetail = true;
    private String postRef;
    private static int mLikeCount;
    private static int mFollowing;
    private int mFollowers;

    private final ChildEventListener userChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            try {
                String name = dataSnapshot.child(User.COLUMN_NAME).getValue().toString();
                String imageUrl = dataSnapshot.child(User.COLUMN_IMAGE_URL).getValue().toString();
                updateUserInfo(name, imageUrl);
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
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_full_screen_image, container, false);
        ButterKnife.bind(this, rootView);
        setupToolbar();
        if (getActivity().getIntent().getData() != null){
            LoadBitmapAsyncTask loadBitmapAsyncTask = new LoadBitmapAsyncTask();
            loadBitmapAsyncTask.execute();
        }

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance()
                        .getReference(Constants.FAVORITE_TABLE_NAME + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "/" + postRef);
                DatabaseReference userRef = FirebaseDatabase.getInstance()
                        .getReference(User.TABLE_NAME + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "/" + User.COLUMN_LIKES);

                if (mFavoriteButton.getTag() == getString(R.string.unfavorite_tag)) {
                    //if initial was unfavorite then change it to favorite
                    mFavoriteButton.setImageResource(R.drawable.ic_heart_colored);
                    mFavoriteButton.setTag(getString(R.string.favorite_tag));
                    userRef.setValue(++mLikeCount);
                    reference.setValue(Constants.TRUE_STR);
                } else {
                    mFavoriteButton.setImageResource(R.drawable.ic_heart);
                    mFavoriteButton.setTag(getString(R.string.unfavorite_tag));
                    if (mLikeCount > 0) {
                        userRef.setValue(--mLikeCount);
                    }
                    reference.setValue(Constants.FALSE_STR);
                }

            }
        });

        if (getActivity().getIntent().getParcelableExtra(POST_EXTRA) != null){
            mPost = getActivity().getIntent().getParcelableExtra(POST_EXTRA);
            //query database to see if user is there
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(User.TABLE_NAME);
            Query userQuery = userRef.orderByKey().equalTo(mPost.getUid());
            userQuery.addChildEventListener(userChildEventListener);
            showDetail = true;
            displayPost();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Post.TABLE_NAME);
            Query query = reference.orderByChild(Post.COLUMN_TITLE).equalTo(mPost.getTitle());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postDataSnapShot: dataSnapshot.getChildren()){
                        if (postDataSnapShot.child(Post.COLUMN_TITLE).getValue().toString().equals(mPost.getTitle()) &&
                                postDataSnapShot.child(Post.COLUMN_UID).getValue().toString().equals(mPost.getUid()) &&
                                postDataSnapShot.child(Post.COLUMN_CATEGORY).getValue().toString().equals(mPost.getCategory())) {
                            postRef = postDataSnapShot.getRef().getKey();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            DatabaseReference favoriteReference = FirebaseDatabase.getInstance()
                    .getReference(Constants.FAVORITE_TABLE_NAME + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            favoriteReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot favoriteSnapshot: dataSnapshot.getChildren()){
                        if (favoriteSnapshot.getKey().equals(postRef)) {
                            updateFavoriteButton(favoriteSnapshot.getValue().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference userReference = FirebaseDatabase.getInstance()
                    .getReference(User.TABLE_NAME + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot user: dataSnapshot.getChildren()){
                        if (user.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            mLikeCount = Integer.parseInt(user.child(User.COLUMN_LIKES).toString());
                            mFollowing = Integer.parseInt(user.child(User.COLUMN_FOLLOWING).toString());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference postUserRef = FirebaseDatabase.getInstance()
                    .getReference(User.TABLE_NAME + "/" + mPost.getUid());
            postUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot user: dataSnapshot.getChildren()){
                        if (user.getKey().equals(mPost.getUid())){
                            mFollowers = Integer.parseInt(user.child(User.COLUMN_FOLLOWERS).toString());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        mGestureDetector = new GestureDetector(getContext(),new GestureListener(getContext()));
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

        return rootView;
    }

    private void updateFavoriteButton(String value){
        if (value.equals(Constants.TRUE_STR)){
            mFavoriteButton.setImageResource(R.drawable.ic_heart_colored);
            mFavoriteButton.setTag(getString(R.string.favorite_tag));
        } else {
            mFavoriteButton.setImageResource(R.drawable.ic_heart);
            mFavoriteButton.setTag(getString(R.string.unfavorite_tag));
        }
    }

    private void updateUserInfo(String name, String imageUrl){
        if (!(name.equals(""))){
            mSubmitterTextView.setText(name);
        } else {
            mSubmitterTextView.setVisibility(View.GONE);
        }

        if (!(imageUrl.equals(""))){
            Glide.with(getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_image)
                    .into(mProfileImageView);
        } else {
            mProfileImageView.setVisibility(View.GONE);
        }
    }

    private void displayPost(){
        if (mPost != null){
            Glide.with(getContext())
                    .load(mPost.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_image)
                    .into(mImageView);
            if (!(mPost.getTitle().equals(""))) {
                mTitleTextView.setText(mPost.getTitle());
            } else {
                mTitleTextView.setVisibility(View.GONE);
            }

            if (!(mPost.getLocation().equals(""))){
                final String location = mPost.getLocation().replaceAll("\\n",", ");
                mLocationTextView.setText(location);
                mLocationTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + location);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });

            } else {
                mLocationTextView.setVisibility(View.GONE);
            }

            if (!(mPost.getDescription().equals(""))){
                mDescriptionTextView.setText(mPost.getDescription());
            } else {
                mDescriptionTextView.setVisibility(View.GONE);
            }

            if (!(mPost.getDate().equals(""))){
                mDateTextView.setText(DateUtils.getFriendlyDate(mPost.getDate()));
            } else {
                mDateTextView.setVisibility(View.GONE);
            }

            if (!(mPost.getCategory().equals(""))){
                mCategoryTextView.setText(mPost.getCategory());
            } else {
                mCategoryTextView.setVisibility(View.GONE);
            }

            if (!(mPost.getTags().equals(""))){
                mTagsTextView.setText(mPost.getTags());
            } else {
                mTagsTextView.setVisibility(View.GONE);
            }
        }
    }

    private void setupToolbar(){
        mToolbar = FragmentUtils.getToolbar(getContext(), mToolbar, false);
        mToolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        hideToolbar(); //TODO find a better way to do this
        hideToolbar(); //kind of a hack way to show immersive content
    }

    public void hideToolbar(){
        View decorView = getActivity().getWindow().getDecorView();
        if (mToolbar.getVisibility() == View.VISIBLE){
            // hide UI
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            if (showDetail) {
                mDetailLinearLayout.setVisibility(View.INVISIBLE);
            }

            mToolbar.setVisibility(View.INVISIBLE);
            mAppBarLayout.setVisibility(View.INVISIBLE);
        } else {
            // show UI
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            if (showDetail) {
                mDetailLinearLayout.setVisibility(View.VISIBLE);
            }
            mToolbar.setVisibility(View.VISIBLE);
            mAppBarLayout.setVisibility(View.VISIBLE);
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int resourceId;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            if (mPost == null){
                //user is just posting
                resourceId = R.menu.full_screen_image_post_menu;
            } else if (currentUser.getUid().equals(mPost.getUid())){
                //user is viewing his own post
                resourceId = R.menu.full_screen_image_current_menu;
            } else {
                //user is viewing someone else post
                resourceId = R.menu.full_screen_image_menu;
            }
        } catch (NullPointerException e){
            e.printStackTrace();
            resourceId = R.menu.full_screen_image_menu;
        }

        inflater.inflate(resourceId, menu);
        initializeFollowingMenutItem(menu.findItem(R.id.action_follow_user));
    }

    private void initializeFollowingMenutItem(final MenuItem menuItem){
        if (menuItem != null) {
            DatabaseReference followingRef = FirebaseDatabase.getInstance()
                    .getReference(Constants.FOLLOWING_TABLE_NAME + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot followingSnapshot : dataSnapshot.getChildren()) {
                        if (followingSnapshot.getKey().equals(mPost.getUid())) {
                            if (followingSnapshot.getValue().equals(Constants.TRUE_STR)){
                                menuItem.setIcon(R.drawable.ic_user_following);
                                menuItem.setTitle(getString(R.string.unfollow_user));
                            } else {
                                menuItem.setIcon(R.drawable.ic_follow_user);
                                menuItem.setTitle(getString(R.string.follow_user));
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_set_wallpaper:
                setImage();
                return true;
            case R.id.action_follow_user:
                followUser(item);
                return true;
            case R.id.action_delete:
                showAlertDialog();
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_AlertDialog).create();
        alertDialog.setTitle(getString(R.string.delete_alert));
        alertDialog.setMessage(getString(R.string.delete_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.no),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.yes),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost();
                    }
                });
        alertDialog.show();
        alertDialog.getWindow().setLayout(890, 525);
    }

    private void followUser(MenuItem item){
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(User.TABLE_NAME + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "/" + User.COLUMN_FOLLOWING);
        DatabaseReference postRef = FirebaseDatabase.getInstance()
                .getReference(User.TABLE_NAME + "/" + mPost.getUid() +
                        "/" + User.COLUMN_FOLLOWERS);
        DatabaseReference followingRef = FirebaseDatabase.getInstance()
                .getReference(Constants.FOLLOWING_TABLE_NAME + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "/" + mPost.getUid());
        DatabaseReference followerRef = FirebaseDatabase.getInstance()
                .getReference(Constants.FOLLOWER_TABLE_NAME + "/" + mPost.getUid() +
                        "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (item.getTitle().equals(getString(R.string.follow_user))){
            item.setIcon(R.drawable.ic_user_following);
            Snackbar.make(mCoordinatorLayout, getString(R.string.following), Snackbar.LENGTH_SHORT).show();
            item.setTitle(getString(R.string.unfollow_user));
            userRef.setValue(++mFollowing);
            followingRef.setValue(Constants.TRUE_STR);
            followerRef.setValue(Constants.TRUE_STR);
            postRef.setValue(++mFollowers);
        } else if (item.getTitle().equals(getString(R.string.unfollow_user))){
            item.setIcon(R.drawable.ic_follow_user);
            item.setTitle(getString(R.string.follow_user));
            Snackbar.make(mCoordinatorLayout, getString(R.string.unfollow_user), Snackbar.LENGTH_SHORT).show();
            if (mFollowing > 0) {
                userRef.setValue(--mFollowing);
            }
            if (mFollowers > 0){
                postRef.setValue(--mFollowers);
            }
            followingRef.setValue(Constants.FALSE_STR);
            followerRef.setValue(Constants.FALSE_STR);
        }


    }

    private void deletePost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Post.TABLE_NAME);
        Query query = reference.orderByChild(Post.COLUMN_TITLE).equalTo(mPost.getTitle());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postDataSnapShot: dataSnapshot.getChildren()){
                    if (postDataSnapShot.child(Post.COLUMN_TITLE).getValue().toString().equals(mPost.getTitle()) &&
                            postDataSnapShot.child(Post.COLUMN_UID).getValue().toString().equals(mPost.getUid()) &&
                            postDataSnapShot.child(Post.COLUMN_CATEGORY).getValue().toString().equals(mPost.getCategory())) {
                        postDataSnapShot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Intent returnIntent = new Intent();
        returnIntent.putExtra(IMAGE_DELETE_KEY, mPost.getTitle());
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }

    public void setImage(){
        try {
            if (mSelectedBitmap != null) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
                wallpaperManager.setBitmap(mSelectedBitmap);
                Snackbar snackbar = DisplayUtils.showSnackBar(mCoordinatorLayout, getString(R.string.set_wallpaper_successful));
                snackbar.show();
            } else if (mPost != null){
                Intent setWallpaperIntent = new Intent(getActivity(), SetWallpaperIntentService.class);
                setWallpaperIntent.putExtra(SetWallpaperIntentService.WALLPAPER_EXTRA, mPost.getImageUrl());
                getActivity().startService(setWallpaperIntent);

            }
        } catch (IOException e) {
            Snackbar snackbar = DisplayUtils.showSnackBar(mCoordinatorLayout, getString(R.string.set_wallpaper_failed));
            snackbar.show();
            e.printStackTrace();
        }
    }



    /**
     * Gesture Listener
     * handles gestures for imageView
     */
    class GestureListener implements GestureDetector.OnGestureListener,
            GestureDetector.OnDoubleTapListener{
        private Context mContext;

        GestureListener(Context context){
            this.mContext = context;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            hideToolbar();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //todo handle zoom
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

    }

    class LoadBitmapAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mImageView.setImageBitmap(mSelectedBitmap);
            showDetail = false;
            mDetailLinearLayout.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Uri selectedImageUri = getActivity().getIntent().getData();
                if (mSelectedBitmap != null) {
                    mSelectedBitmap.recycle();
                }
                mSelectedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
