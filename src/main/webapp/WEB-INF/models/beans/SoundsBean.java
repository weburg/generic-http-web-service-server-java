package beans;

import com.weburg.domain.Sound;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SoundsBean implements Serializable {
    private List<Sound> sounds = new ArrayList<>();

    public SoundsBean() {}

    public List<Sound> getSounds() {
        return sounds;
    }

    public void setSounds(List<Sound> sounds) {
        this.sounds = sounds;
    }
}
