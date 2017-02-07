package com.maheshgaya.android.coolwallpapers.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.maheshgaya.android.coolwallpapers.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    /** handles app navigation: home, search, profile */
    @BindView(R.id.bottom_navigation)BottomNavigationView mBottomNavigation;
    /** handles main container for adding fragments */
    FragmentManager mFragmentManager;

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


}
