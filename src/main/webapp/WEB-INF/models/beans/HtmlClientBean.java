package beans;

import example.domain.Sound;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HtmlClientBean implements Serializable {
    public HtmlClientBean() {}

    private Object formData = new Object();
    private List<Sound> sounds = new ArrayList<>();
    private String audioTypesList = "";
    private String imageTypesList = "";
    private String videoTypesList = "";

    public Object getFormData() {
        return formData;
    }

    public void setFormData(Object formData) {
        this.formData = formData;
    }

    public List<Sound> getSounds() {
        return sounds;
    }

    public void setSounds(List<Sound> sounds) {
        this.sounds = sounds;
    }

    public String getAudioTypesList() {
        return audioTypesList;
    }

    public void setAudioTypesList(String audioTypesList) {
        this.audioTypesList = audioTypesList;
    }

    public String getImageTypesList() {
        return imageTypesList;
    }

    public void setImageTypesList(String imageTypesList) {
        this.imageTypesList = imageTypesList;
    }

    public String getVideoTypesList() {
        return videoTypesList;
    }

    public void setVideoTypesList(String videoTypesList) {
        this.videoTypesList = videoTypesList;
    }
}