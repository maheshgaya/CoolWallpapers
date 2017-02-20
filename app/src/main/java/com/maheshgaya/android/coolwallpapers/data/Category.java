package com.maheshgaya.android.coolwallpapers.data;

/**
 * Created by Mahesh Gaya on 2/19/17.
 */

public class Category {
    private int drawableInt;
    private String name;

    public Category(String name, int drawableInt){
        this.drawableInt = drawableInt;
        this.name = name;
    }

    public int getDrawableInt() {
        return drawableInt;
    }

    public void setDrawableInt(int imageUrl) {
        this.drawableInt = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
