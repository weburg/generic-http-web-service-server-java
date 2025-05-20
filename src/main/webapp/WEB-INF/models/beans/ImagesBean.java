package beans;

import example.domain.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImagesBean implements Serializable {
    private List<Image> images = new ArrayList<>();

    public ImagesBean() {}

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
