package beans;

import java.io.Serializable;

public class TrucksBean implements Serializable {
    private String result = "";

    public TrucksBean() {}

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
