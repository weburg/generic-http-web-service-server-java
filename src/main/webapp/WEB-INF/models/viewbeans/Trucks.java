package viewbeans;

import java.io.Serializable;

public class Trucks implements Serializable {
    private String result = "";

    public Trucks() {}

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
