package com.maheshgaya.android.coolwallpapers.data;

/**
 * Created by Mahesh Gaya on 2/13/17.
 */

public class User {
    private String uid;
    private String name;
    private String imageUrl;

    public static final String TABLE_NAME = "users";
    public User(String uid, String name, String imageUrl){
        this.uid = uid;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
