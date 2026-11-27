package viewbeans;

import example.domain.TruckRaceResult;

import java.io.Serializable;

public class Trucks implements Serializable {
    TruckRaceResult truckRaceResult;

    public Trucks() {}

    public TruckRaceResult getTruckRaceResult() {
        return truckRaceResult;
    }

    public void setTruckRaceResult(TruckRaceResult truckRaceResult) {
        this.truckRaceResult = truckRaceResult;
    }
}
