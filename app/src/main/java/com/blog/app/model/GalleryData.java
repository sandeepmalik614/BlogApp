package com.blog.app.model;

import java.io.Serializable;

public class GalleryData implements Serializable {

    private String image;

    private boolean selected;

    public GalleryData(String image, boolean selected) {
        this.image = image;
        this.selected = selected;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
