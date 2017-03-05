package com.maheshgaya.android.coolwallpapers.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.Category;

import java.util.ArrayList;

/**
 * Created by Mahesh Gaya on 2/19/17.
 */

public class CategoryAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Category>> {
    private static final String TAG = CategoryAsyncTaskLoader.class.getSimpleName();
    public static final int HEADER_TITLE = -1;
    private int[] mCategoryIcons = {
            HEADER_TITLE,
            android.R.color.holo_blue_dark,
            android.R.color.holo_red_light,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_purple,
    };

    public CategoryAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Category> loadInBackground() {
        ArrayList<Category> mCategoryList = new ArrayList<>();
        String[] categoryArray = getContext().getApplicationContext().getResources().getStringArray(R.array.category_array);
        for (int i = 0; i < categoryArray.length; i++){
            mCategoryList.add(new Category(categoryArray[i], mCategoryIcons[i]));
        }
        return mCategoryList;

    }


}
