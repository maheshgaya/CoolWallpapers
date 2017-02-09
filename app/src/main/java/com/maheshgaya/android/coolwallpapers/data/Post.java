package com.maheshgaya.android.coolwallpapers.data;

/**
 * Created by Mahesh Gaya on 2/8/17.
 */

public class Post {

    public static final String TABLE_NAME = "posts";

    /** keeps a reference to the image on the Storage */
    private String imageUrl;
    /** keeps a reference to the submitter */
    private String uid;
    /** the title of the post, should be very short */
    private String title;
    /** date posted */
    private String date;
    /** location, if given by the user */
    private String location;
    /** description of the post */
    private String description;
    /** category of the post */
    private String category;
    /** tags, if any */
    private String tags;

    /**
     * Only constructor
     * @param uid
     * @param title
     * @param date
     * @param description
     * @param category
     * @param tags
     * @param location
     */
    public Post(String uid, String title,  String imageUrl, String date, String description,
                String category, String tags, String location){
        this.uid = uid;
        this.title = title;
        this.imageUrl = imageUrl;
        this.date = date;
        this.description = description;
        this.category = category;
        this.tags = tags;
        this.location = location;

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
