package viewbeans;

import example.domain.Omnibus;

import java.io.Serializable;

public class Omnibusses implements Serializable {
    private Omnibus omnibusses = null;

    public Omnibusses() {}

    public Omnibus getOmnibusses() {
        return omnibusses;
    }

    public void setOmnibusses(Omnibus omnibus) {
        this.omnibusses = omnibus;
    }
}
