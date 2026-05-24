package example.domain;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

public class Omnibus implements Serializable {
    public Omnibus() {}

    private static final long serialVersionUID = 1L;

    private LocalDateTime birthtime = null;
    private OffsetDateTime sendtime = null;
    private List<String> toppings = new ArrayList<>();
    private String[] sides = new String[0];
    private Boolean isOnFire = false;
    private File document = null;
    private Map<String, String> pairing = new LinkedHashMap<>();

    public LocalDateTime getBirthtime() {
        return birthtime;
    }

    public void setBirthtime(LocalDateTime birthtime) {
        this.birthtime = birthtime;
    }

    public OffsetDateTime getSendtime() {
        return sendtime;
    }

    public void setSendtime(OffsetDateTime sendtime) {
        this.sendtime = sendtime;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public void setToppings(List<String> toppings) {
        this.toppings = toppings;
    }

    public String[] getSides() {
        return sides;
    }

    public void setSides(String[] sides) {
        this.sides = sides;
    }

    public Boolean getOnFire() {
        return isOnFire;
    }

    public void setOnFire(Boolean onFire) {
        isOnFire = onFire;
    }

    public File getDocument() {
        return document;
    }

    public void setDocument(File document) {
        this.document = document;
    }

    public Map<String, String> getPairing() {
        return pairing;
    }

    public void setPairing(Map<String, String> pairing) {
        this.pairing = pairing;
    }
}
