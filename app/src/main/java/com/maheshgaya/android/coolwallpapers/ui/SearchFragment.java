package com.maheshgaya.android.coolwallpapers.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maheshgaya.android.coolwallpapers.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/6/17.
 */

public class SearchFragment extends Fragment {
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.toolbar_title)TextView mToolbarTitle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbarTitle.setText(getString(R.string.bottom_nav_search));
        //TODO: if add menu remove this code for content insets
        mToolbar.setContentInsetsAbsolute(0, 0);
        mToolbar.setContentInsetsRelative(0, 0);
        return rootView;
    }
}
