package example.domain;

import example.SupportedMimeTypes;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public abstract class Multimedia {
    protected String name = "";
    protected String caption = "";
    protected transient File mediaFile = new File("");
    protected transient SupportedMimeTypes.MimeTypes mimeType;

    protected Multimedia(SupportedMimeTypes.MimeTypes mimeType) {
        this.mimeType = mimeType;
    }

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getCaption();

    public abstract void setCaption(String caption);

    public final File getMediaFile() {
        return this.mediaFile;
    }

    public final void setMediaFile(File mediaFile) {
        if (mediaFile != null && (this.name == null || this.name.isEmpty())) {
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

    public SupportedMimeTypes.MimeTypes getMimeType() {
        return this.mimeType;
    }
}