package example.domain;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Image implements Serializable {
    public Image() {}

    private static final long serialVersionUID = 1L;

    private String name = "";
    private String caption = "";
    private transient File imageFile = new File("");

    public String getName() {
        return name;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public File getImageFile() {
        return this.imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
        this.name = imageFile.getName();

        try {
            File captionFile = new File(imageFile.getAbsolutePath() + ".txt");

            if ((getCaption() == null || getCaption().isEmpty()) && captionFile.exists() && captionFile.isFile()) {
                setCaption(FileUtils.readFileToString(captionFile));
            }
        } catch (IOException e) {
            // If file didn't exist, then, let caption be what it was
        }
    }
}