package com.maheshgaya.android.coolwallpapers.ui.post;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/8/17.
 */

public class PostFragment extends Fragment {
    private static final String TAG = PostFragment.class.getSimpleName();

    /** Views */
    @BindView(R.id.title_edittext)EditText editText;
    @BindView(R.id.post_toolbar)Toolbar mToolbar;

    /**
     * Enable menus on toolbar
     */
    public PostFragment(){ setHasOptionsMenu(true); }

    /**
     * initialize the views
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        ButterKnife.bind(this, rootView);
        ((PostActivity) getActivity()).setSupportActionBar(mToolbar);
        ((PostActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((PostActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        ((PostActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return rootView;
    }

    /**
     * creates the menu for Toolbar
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.post_menu, menu);
        MenuItem postItem = menu.findItem(R.id.action_post);
        postItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }
}
