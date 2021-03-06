package com.maheshgaya.android.coolwallpapers.util;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.ui.main.HomeFragment;
import com.maheshgaya.android.coolwallpapers.ui.main.ProfileFragment;
import com.maheshgaya.android.coolwallpapers.ui.main.SearchFragment;

/**
 * Created by Mahesh Gaya on 2/8/17.
 */

public class FragmentUtils {
    private static final String TAG = FragmentUtils.class.getSimpleName();
    public static int getBottomNavigationItemId(int currentFragmentId){
        switch (currentFragmentId){
            case R.id.menu_search:
                return 1;
            case R.id.menu_profile:
                return 2;
            default:
                return 0; //if nothing is selected, home is returned
        }
    }

    /**
     * return a fragment instance based on the MenuItemId passed
     * @param itemId
     * @return
     */
    public static Fragment selectFragment(int itemId) {
        switch (itemId){
            case R.id.menu_home: {
                return new HomeFragment();
            }
            case R.id.menu_search: {
                return new SearchFragment();
            }
            case R.id.menu_profile: {
                return new ProfileFragment();
            }
            default:
                return null;
        }
    }


    public static Toolbar getToolbar(Context context, Toolbar toolbar, boolean parent){
        ((AppCompatActivity) context).setSupportActionBar(toolbar);
        try {
            ((AppCompatActivity) context).getSupportActionBar().setHomeButtonEnabled(!parent);
            ((AppCompatActivity) context).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(!parent);
            ((AppCompatActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(!parent);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return toolbar;
    }

    public static Toolbar getToolbar(Context context, Toolbar toolbar, String title){
        ((AppCompatActivity) context).setSupportActionBar(toolbar);
        toolbar.setTitle(title);
        return toolbar;
    }

}
