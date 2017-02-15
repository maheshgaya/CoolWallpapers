package com.maheshgaya.android.coolwallpapers.ui.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maheshgaya.android.coolwallpapers.BuildConfig;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.User;
import com.maheshgaya.android.coolwallpapers.util.DatabaseUtils;
import com.maheshgaya.android.coolwallpapers.util.FragmentUtils;
import com.maheshgaya.android.coolwallpapers.util.UserAuthUtils;

import java.util.Arrays;

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

//    /**
//     * redirects to Login Activity
//     * Account Providers: email, Google
//     */
//    private void requireLogin(){
//        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
//        int status  = googleApiAvailability.isGooglePlayServicesAvailable(this);
//        if (status == ConnectionResult.API_UNAVAILABLE || status != ConnectionResult.SUCCESS){
//            //TODO replace small icon
//            //If Google Play Services is not available, app should finish
//            //Otherwise, there will be memory leaks
//            //However, show the user that the app failed as a notification
//            NotificationCompat.Builder notificationBuilder =
//                    (NotificationCompat.Builder) new NotificationCompat.Builder(this)
//                            .setSmallIcon(R.drawable.ic_mail_white_24dp) //icon for app
//                            .setContentTitle(getString(R.string.google_play_services)) //subject
//                            .setContentText(getString(R.string.no_google_play_services_available)) //text, one line view
//                            .setDefaults(Notification.DEFAULT_ALL) //Allows vibrate
//                            .setStyle(new NotificationCompat.BigTextStyle()
//                                    .bigText(getString(R.string.no_google_play_services_available))) //text, multi-line view
//                            .setAutoCancel(true); //cancels if user opens app
//
//            NotificationManager mNotificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify(GOOGLE_API_NOTIFICATION_ID, notificationBuilder.build());
//
//            if (Build.VERSION.SDK_INT >= 21) {
//                finishAndRemoveTask();
//            } else {
//                finish();
//            }
//        }
//        startActivityForResult(
//                // Get an instance of AuthUI based on the default app
//                AuthUI.getInstance().createSignInIntentBuilder()
//                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
//                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
//                        .setTheme(R.style.AuthTheme)
//                        .build(),
//                RC_SIGN_IN);
//    }

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
    }

//    /** Utilities for getting status of the Firebase */
//    /**
//     * Allow the user to sign out.
//     * This redirects to login activity
//     */
//    public void signOut(){
//        //todo cleanup before
//        UserAuthUtils.signOut(this);
//        requireLogin();
//    }


}
