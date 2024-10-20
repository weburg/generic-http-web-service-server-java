package com.weburg.domain;

import java.io.File;
import java.io.Serializable;

public class Sound implements Serializable {
    public Sound() {}

    private File soundFile;

    public File getSoundFile() {
        return this.soundFile;
    }

    public void setSoundFile(File soundFile) {
        this.soundFile = soundFile;
    }
}