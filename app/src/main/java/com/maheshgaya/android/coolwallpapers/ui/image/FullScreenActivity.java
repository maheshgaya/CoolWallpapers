package com.maheshgaya.android.coolwallpapers.ui.image;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.AsyncTaskLoader;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.util.DisplayUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/12/17.
 */

public class FullScreenActivity extends AppCompatActivity {
    private static final String TAG = FullScreenActivity.class.getSimpleName();
    @BindView(R.id.full_screen_image_view)ImageView mImageView;
    @BindView(R.id.full_screen_toolbar)Toolbar mToolbar;
    @BindView(R.id.layout_full_screen_image)CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.full_screen_appbarlayout)AppBarLayout mAppBarLayout;

    private GestureDetector mGestureDetector;
    private Bitmap mSelectedBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hideStatusBar();

        if (getIntent().getData() != null){
            try {

                Uri selectedImageUri = getIntent().getData();
                if (mSelectedBitmap != null) {
                    mSelectedBitmap.recycle();
                }
                mSelectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                mImageView.setImageBitmap(mSelectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        mGestureDetector = new GestureDetector(this, new GestureListener(this));
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    private void hideStatusBar(){
        View decorView = getWindow().getDecorView();
        // make the status bar low profile
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (mToolbar.getVisibility() == View.VISIBLE){
            mToolbar.setVisibility(View.GONE);
            mAppBarLayout.setVisibility(View.GONE);
        } else {
            showStatusBar();
            mToolbar.setVisibility(View.VISIBLE);
            mAppBarLayout.setVisibility(View.VISIBLE);
        }
    }
    private void showStatusBar(){
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.full_screen_image_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_set_wallpaper:
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                try {
                    if (mSelectedBitmap != null) {
                        wallpaperManager.setBitmap(mSelectedBitmap);
                        Snackbar snackbar = DisplayUtils.showSnackBar(mCoordinatorLayout, getString(R.string.set_wallpaper_successful));
                        snackbar.show();
                    }
                } catch (IOException e) {
                    Snackbar snackbar = DisplayUtils.showSnackBar(mCoordinatorLayout, getString(R.string.set_wallpaper_failed));
                    snackbar.show();
                    e.printStackTrace();
                }
                return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Gesture Listener
     * handles gestures for imageView
     */
    public class GestureListener implements GestureDetector.OnGestureListener,
            GestureDetector.OnDoubleTapListener{
        private Activity mActivity;

        GestureListener(Activity activity){
            this.mActivity = activity;
        }

        //TODO finish activity if user drags down
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            hideStatusBar();
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
            Toast.makeText(getApplicationContext(), "Double Tap. Yay!", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    }


}
