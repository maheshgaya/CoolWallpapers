package com.maheshgaya.android.coolwallpapers.ui.image;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.Post;
import com.maheshgaya.android.coolwallpapers.data.User;
import com.maheshgaya.android.coolwallpapers.service.SetWallpaperIntentService;
import com.maheshgaya.android.coolwallpapers.ui.post.PostActivity;
import com.maheshgaya.android.coolwallpapers.util.DateUtils;
import com.maheshgaya.android.coolwallpapers.util.DisplayUtils;
import com.maheshgaya.android.coolwallpapers.util.UserAuthUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/21/17.
 */

public class FullScreenFragment extends Fragment {
    private static final String TAG = FullScreenActivity.class.getSimpleName();
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
            try {
                //todo push this to intent service
                Uri selectedImageUri = getActivity().getIntent().getData();
                if (mSelectedBitmap != null) {
                    mSelectedBitmap.recycle();
                }
                mSelectedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                mImageView.setImageBitmap(mSelectedBitmap);
                showDetail = false;
                mDetailLinearLayout.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Hello World", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= 21) {
                    if (mFavoriteButton.getTag() == getString(R.string.unfavorite_tag)) {
                        mFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_colored, null));
                        mFavoriteButton.setTag(getString(R.string.favorite_tag));
                    } else {
                        mFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart, null));
                        mFavoriteButton.setTag(getString(R.string.unfavorite_tag));
                    }
                } else {
                    if (mFavoriteButton.getTag() == getString(R.string.unfavorite_tag)) {
                        mFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_colored));
                        mFavoriteButton.setTag(getString(R.string.favorite_tag));
                    } else {
                        mFavoriteButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart));
                        mFavoriteButton.setTag(getString(R.string.unfavorite_tag));
                    }
                }
            }
        });

        if (getActivity().getIntent().getParcelableExtra(POST_EXTRA) != null){
            mPost = getActivity().getIntent().getParcelableExtra(POST_EXTRA);
            //query database to see if user is there
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(User.TABLE_NAME);
            Query query = userRef.orderByKey().equalTo(mPost.getUid());
            query.addChildEventListener(userChildEventListener);
            showDetail = true;
            displayPost();
            Log.d(TAG, "onCreateView: " + mPost.toString());
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
                String location = mPost.getLocation();
                location = location.replaceAll("\\n",", ");
                mLocationTextView.setText(location);
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
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        inflater.inflate(R.menu.full_screen_image_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_set_wallpaper:
                setImage();
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
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
            Toast.makeText(mContext, "Double Tap. Yay!", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

    }

}
