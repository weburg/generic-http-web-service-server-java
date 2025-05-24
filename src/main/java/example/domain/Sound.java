package example.domain;

import example.SupportedMimeTypes;

import java.beans.BeanProperty;
import java.io.File;
import java.io.Serializable;

public class Sound extends Multimedia implements Serializable {
    public Sound() {
        super(SupportedMimeTypes.MimeTypes.AUDIO);
    }

    private static final long serialVersionUID = 1L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @BeanProperty(description = "File to send. Required, but not included in output.")
    public File getSoundFile() {
        return this.mediaFile;
    }

    public void setSoundFile(File imageFile) {
        setMediaFile(imageFile);
    }
}