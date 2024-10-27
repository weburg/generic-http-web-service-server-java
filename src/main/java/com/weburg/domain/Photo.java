package com.weburg.domain;

import java.io.File;
import java.io.Serializable;

public class Photo implements Serializable {
    public Photo() {}

    private String name;
    private String caption = "";
    private transient File photoFile;

    public String getName() {
        return name;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public File getPhotoFile() {
        return this.photoFile;
    }

    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
        this.name = photoFile.getName();
    }
}