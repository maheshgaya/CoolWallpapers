package com.maheshgaya.android.coolwallpapers.data;

/**
 * Created by Mahesh Gaya on 2/13/17.
 */

public class User {
    private String uid;
    private String name;
    private String email;
    private String imageUrl;
    private int followers;
    private int following;
    private int likes;

    public static final String TABLE_NAME = "users";
    /** columns names should match the variable names */
    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_IMAGE_URL = "imageUrl";
    public static final String COLUMN_FOLLOWERS = "followers";
    public static final String COLUMN_FOLLOWING = "following";
    public static final String COLUMN_LIKES = "likes";

    public User(String uid, String name, String email, String imageUrl){
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.followers = 0;
        this.following = 0;
        this.likes = 0;
    }
    public User(String uid, String name, String email, String imageUrl, int followers,
                int following, int likes){
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.followers = followers;
        this.following = following;
        this.likes = likes;
    }
    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void incrementFollowers(){
        this.followers++;
    }

    public void incrementFollowing(){
        this.following++;
    }

    public void incrementLikes(){
        this.likes++;
    }

    public void decrementFollowers(){
        this.followers--;
    }

    public void decrementFollowing(){
        this.following--;
    }

    public void decrementLikes(){
        this.likes--;
    }
}
