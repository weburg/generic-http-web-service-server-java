package example.domain;

import java.io.Serializable;

public class TruckRaceResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Truck truck1 = null;
    private Truck truck2 = null;
    private Truck winner = null;

    public TruckRaceResult() {}

    public Truck getTruck1() {
        return truck1;
    }

    public void setTruck1(Truck truck1) {
        this.truck1 = truck1;
    }

    public Truck getTruck2() {
        return truck2;
    }

    public void setTruck2(Truck truck2) {
        this.truck2 = truck2;
    }

    public Truck getWinner() {
        return winner;
    }

    public void setWinner(Truck winner) {
        this.winner = winner;
    }
}
