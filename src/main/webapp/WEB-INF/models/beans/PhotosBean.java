package beans;

import com.weburg.domain.Photo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhotosBean implements Serializable {
	private List<Photo> photos = new ArrayList<>();

	public PhotosBean() {}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}
}
