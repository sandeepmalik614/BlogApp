package com.blog.app.model;

public class User {

    private String firebaseId;

    private String name;

    private String mobile;

    private String userImage;

    private String creationDate;

    public User() {
    }

    public User(String firebaseId, String name, String mobile, String userImage, String creationDate) {
        this.firebaseId = firebaseId;
        this.name = name;
        this.mobile = mobile;
        this.userImage = userImage;
        this.creationDate = creationDate;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
