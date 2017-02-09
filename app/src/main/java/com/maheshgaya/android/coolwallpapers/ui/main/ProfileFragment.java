package com.maheshgaya.android.coolwallpapers.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.maheshgaya.android.coolwallpapers.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/6/17.
 */

public class ProfileFragment extends Fragment {
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
    @BindView(R.id.likes_textview)TextView mLikesTextView;

    /** gets the current user */
    private FirebaseUser mUser;
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
                    if (mUser != null) {
                        mToolbarTitle.setText(mUser.getDisplayName());
                    }
                } else {
                    mToolbarTitle.setText(getActivity().getString(R.string.bottom_nav_profile));
                }
            }
        });

        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: change this
                displaySnackBar("Test: button is working");
            }
        });

        //TODO: Add gridview and Initialize it

        return rootView;
    }


    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUser = ((MainActivity)getActivity()).getCurrentUser();
        if (mUser != null) {
            mProfileNameTextView.setText(mUser.getDisplayName());
            mFollowerTextView.setText(0 + " " + getString(R.string.followers));
            mFollowingTextView.setText(0 + " " + getString(R.string.following));
            mLikesTextView.setText(0 + " " + getString(R.string.likes));

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_add);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: adding");
                //TODO: Start add Post activity
                return true;
            }
        });
        MenuItem signOutMenuItem = menu.findItem(R.id.action_sign_out);
        signOutMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ((MainActivity) getActivity()).signOut();
                return true;
            }
        });
    }


    private void displaySnackBar(String message){
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Hello");
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            snackbar.setActionTextColor(getContext().getColor(android.R.color.holo_green_light));
        } else {
            snackbar.setActionTextColor(getContext().getResources().getColor(android.R.color.holo_green_light));
        }
        snackbar.show();
    }
}