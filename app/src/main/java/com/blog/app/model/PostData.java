package com.blog.app.model;

import java.util.ArrayList;

public class PostData {

    private String postCreationDate;

    private String userId;

    private String postDescription;

    private ArrayList<String> imageUrls;

    public String getPostCreationDate() {
        return postCreationDate;
    }

    public void setPostCreationDate(String postCreationDate) {
        this.postCreationDate = postCreationDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
