package viewbeans;

import example.domain.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Images implements Serializable {
    private List<Image> images = new ArrayList<>();

    public Images() {}

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
