package beans;

import java.io.Serializable;

public class OmnibusBean implements Serializable {
    private String result = "";

    public OmnibusBean() {}

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
