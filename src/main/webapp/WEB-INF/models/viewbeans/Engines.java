package viewbeans;

import example.domain.Engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Engines implements Serializable {
    private List<Engine> engines = new ArrayList<>();

    public Engines() {}

    public List<Engine> getEngines() {
        return engines;
    }

    public void setEngines(List<Engine> engines) {
        this.engines = engines;
    }
}
