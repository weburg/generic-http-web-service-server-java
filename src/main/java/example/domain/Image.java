package example.domain;

import example.SupportedMimeTypes;

import java.beans.BeanProperty;
import java.io.File;
import java.io.Serializable;

public class Image extends Multimedia implements Serializable {
    private static final long serialVersionUID = 1L;

    public Image() {
        super(SupportedMimeTypes.MimeTypes.IMAGE);
    }

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
    public File getImageFile() {
        return this.mediaFile;
    }

    public void setImageFile(File imageFile) {
        setMediaFile(imageFile);
    }
}