package viewbeans;

import java.io.Serializable;

public class Omnibus implements Serializable {
    private String result = "";

    public Omnibus() {}

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
