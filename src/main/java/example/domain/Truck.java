package example.domain;

import java.io.Serializable;

public class Truck implements Serializable {
    public Truck() {}

    private int id = 0;
    private int engineId = 0;
    private String name = "";

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEngineId() {
        return this.engineId;
    }

    public void setEngineId(int engineId) {
        this.engineId = engineId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}