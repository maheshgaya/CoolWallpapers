package com.maheshgaya.android.coolwallpapers.ui.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseUser;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.ui.image.FullScreenFragment;
import com.maheshgaya.android.coolwallpapers.util.DatabaseUtils;
import com.maheshgaya.android.coolwallpapers.util.FragmentUtils;
import com.maheshgaya.android.coolwallpapers.util.UserAuthUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    /** handles app navigation: home, search, profile */
    @BindView(R.id.bottom_navigation)BottomNavigationView mBottomNavigation;
    /** handles main container for adding fragments */
    FragmentManager mFragmentManager;
    /** saved instance state for fragment */
    private static final String FRAGMENT_KEY = "frag";
    private int mCurrentFragmentId = R.id.menu_home;

    /** failed states for */
    public static final int GOOGLE_API_NOTIFICATION_ID = 200;

    /** Firebase variables */
    public static final int RC_SIGN_IN = 100;
    /** require user account for using the app */

    /** current user */
    private FirebaseUser mUser;

    /**
     * initializes the views and adds listeners for the controls
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialization
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        //get current user
        mUser = UserAuthUtils.getCurrentUser();
        if (mUser == null){
            //redirect to login
            UserAuthUtils.requireLogin(this);
        }

        //bottom navigation initialization
        mFragmentManager = getSupportFragmentManager();

        //initialize the home fragment to the main container
        if (mFragmentManager.findFragmentById(R.id.main_container) == null && savedInstanceState == null){
            mCurrentFragmentId = R.id.menu_home;
            mFragmentManager.beginTransaction()
                    .replace(R.id.main_container, new HomeFragment())
                    .commit();
        } else if (mFragmentManager.findFragmentById(R.id.main_container) != null && savedInstanceState != null){
            //if rotation changes, set the correct selected item for bottom navigation
            mCurrentFragmentId= savedInstanceState.getInt(FRAGMENT_KEY);
            if (mCurrentFragmentId != 0 ){
                mBottomNavigation.getMenu()
                        .getItem(FragmentUtils.getBottomNavigationItemId(mCurrentFragmentId))
                        .setChecked(true);
            }

        }

        //handles what selected item for the bottom navigation bar
        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = FragmentUtils.selectFragment(item.getItemId());
                mCurrentFragmentId = item.getItemId();
                mFragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
                return true;
            }
        });
    }

    /**
     * Save instance for rotation or activity navigation
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saves the current bottom navigation selected
        outState.putInt(FRAGMENT_KEY, mCurrentFragmentId);
    }

    /**
     * check if login was successful
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == ResultCodes.OK) {
                // Successfully signed in
                //initializes the user if login is successful
                mUser = UserAuthUtils.getCurrentUser();
                //add user to database if not already exists there
                DatabaseUtils.addUserToDatabase();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.d(TAG, "onActivityResult: user canceled");
                    return;
                }
                // No network
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    return;
                }
                // Boo boo
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.d(TAG, "onActivityResult: Unknown error");
                }
            }
        }
        if (requestCode == FullScreenFragment.FULL_IMAGE_CODE){
            if (resultCode == ResultCodes.OK){
                String result = data.getStringExtra(FullScreenFragment.IMAGE_DELETE_KEY);
                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                Snackbar.make(rootView, result + " " + getString(R.string.post_deleted), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

}
