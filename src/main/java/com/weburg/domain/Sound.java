package com.weburg.domain;

import java.io.File;
import java.io.Serializable;

public class Sound implements Serializable {
    public Sound() {}

    private String name;
    private transient File soundFile;

    public String getName() {
        return name;
    }

    public File getSoundFile() {
        return this.soundFile;
    }

    public void setSoundFile(File soundFile) {
        this.soundFile = soundFile;
        this.name = soundFile.getName();
    }
}