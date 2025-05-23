package example.domain;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

// TODO this class isn't used yet because mapper doesn't traverse superclasses
public abstract class Multimedia implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String name = "";
    private String caption = "";
    private transient File mediaFile = new File("");

    public String getName() {
        return name;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public File getMediaFile() {
        return this.mediaFile;
    }

    public void setMediaFile(File mediaFile) {
        this.mediaFile = mediaFile;
        this.name = mediaFile.getName();

        try {
            File captionFile = new File(mediaFile.getAbsolutePath() + ".txt");

            if ((getCaption() == null || getCaption().isEmpty()) && captionFile.exists() && captionFile.isFile()) {
                setCaption(FileUtils.readFileToString(captionFile));
            }
        } catch (IOException e) {
            // If file didn't exist, then, let caption be what it was
        }
    }
}
