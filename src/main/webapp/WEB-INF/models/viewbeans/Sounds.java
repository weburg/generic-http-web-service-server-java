package viewbeans;

import example.domain.Sound;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sounds implements Serializable {
    private List<Sound> sounds = new ArrayList<>();

    public Sounds() {}

    public List<Sound> getSounds() {
        return sounds;
    }

    public void setSounds(List<Sound> sounds) {
        this.sounds = sounds;
    }
}
