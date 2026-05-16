package example.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Omnibus implements Serializable {
    public Omnibus() {}

    private static final long serialVersionUID = 1L;

    private LocalDateTime birthtime = LocalDateTime.now();
    private ZonedDateTime sendtime = ZonedDateTime.now();
    private List<String> toppings = new ArrayList<>();
    private String[] sides = new String[0];
    private Boolean isOnFire = false;

    public LocalDateTime getBirthtime() {
        return birthtime;
    }

    public void setBirthtime(LocalDateTime birthtime) {
        this.birthtime = birthtime;
    }

    public ZonedDateTime getSendtime() {
        return sendtime;
    }

    public void setSendtime(ZonedDateTime sendtime) {
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
}
