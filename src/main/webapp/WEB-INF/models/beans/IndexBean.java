package beans;

import com.weburg.domain.Engine;

import java.io.Serializable;

public class IndexBean implements Serializable {
    private Engine engine = new Engine();

    public IndexBean() {}

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    // Java Beans must have a no-arg constructor, be serializable, and have getters/setters

    // Below is how we could force an invalid state if the class requires constructor arguments.
    /*
    public Engine() {
        throw new IllegalStateException("This class requires constructor arguments.");
    }
    */
}
