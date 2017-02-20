package com.maheshgaya.android.coolwallpapers.adapter;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahesh Gaya on 2/19/17.
 */

public class CategoryAsyncTaskLoader extends AsyncTaskLoader<List<Category>> {
    private static final String TAG = CategoryAsyncTaskLoader.class.getSimpleName();
    private List<Category> mCategoryList;
    public static final int HEADER_TITLE = -1;
    //TODO Replace this with actual icons
    private int[] mCategoryIcons = {
            HEADER_TITLE,
            R.drawable.ic_account_circle_black,
            R.drawable.ic_account_circle_black,
            R.drawable.ic_account_circle_black,
            R.drawable.ic_account_circle_black,
            R.drawable.ic_account_circle_black,
    };

    public CategoryAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    public List<Category> loadInBackground() {
        mCategoryList = new ArrayList<>();
        String[] categoryArray = getContext().getApplicationContext().getResources().getStringArray(R.array.category_array);
        for (int i = 0; i < categoryArray.length; i++){
            mCategoryList.add(new Category(categoryArray[i], mCategoryIcons[i]));
        }
        return mCategoryList;

    }


}
