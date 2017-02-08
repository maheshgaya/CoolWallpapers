package com.maheshgaya.android.coolwallpapers.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maheshgaya.android.coolwallpapers.BuildConfig;
import com.maheshgaya.android.coolwallpapers.R;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    /** handles app navigation: home, search, profile */
    @BindView(R.id.bottom_navigation)BottomNavigationView mBottomNavigation;
    /** handles main container for adding fragments */
    FragmentManager mFragmentManager;

    /** Firebase variables */
    private static final int RC_SIGN_IN = 100;
    /** require user account for using the app */
    private FirebaseAuth mFirebaseAuth;
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

        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();
        if (mUser == null){
            //redirect to login
            requireLogin();
        } /*else {
            //TODO: get the picture, name, user id (not necessary here)
            String userName = mUser.getDisplayName();
            Uri userPictureUrl = mUser.getPhotoUrl();
            String uid = mUser.getUid();
        }*/


        mFragmentManager = getSupportFragmentManager();

        //initialize the home fragment to the main container
        if (mFragmentManager.findFragmentById(R.id.main_container) == null){
            mFragmentManager.beginTransaction()
                    .replace(R.id.main_container, new HomeFragment())
                    .commit();
        }

        //handles what selected item for the bottom navigation bar
        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = selectFragment(item);
                mFragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
                return true;
            }
        });
    }

    /**
     * return a fragment instance based on the MenuItem passed
     * @param item
     * @return
     */
    private Fragment selectFragment(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                return new HomeFragment();
            case R.id.menu_search:
                return new SearchFragment();
            case R.id.menu_profile:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    /**
     * redirects to Login Activity
     * Account Providers: email, Google
     */
    private void requireLogin(){
        startActivityForResult(
                // Get an instance of AuthUI based on the default app
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setTheme(R.style.AuthTheme)
                        .build(),
                RC_SIGN_IN);
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
                    return;
                }
            }
        }
    }

    /** Utilities for getting status of the Firebase */

    /**
     * Allow the user to sign out.
     * This redirects to login activity
     */
    public void signOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        Toast.makeText(getApplicationContext(), getString(R.string.sign_out_success), Toast.LENGTH_SHORT).show();
                        //redirect to login activity
                        requireLogin();

                    }
                });
    }

    /**
     * Getter for current user
     * @return
     */
    public FirebaseUser getCurrentUser(){
        return mUser;
    }

}
