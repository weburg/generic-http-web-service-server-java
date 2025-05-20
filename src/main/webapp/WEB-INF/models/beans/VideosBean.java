package beans;

import example.domain.Video;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideosBean implements Serializable {
    private List<Video> videos = new ArrayList<>();

    public VideosBean() {}

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
