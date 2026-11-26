package viewbeans;

import java.io.Serializable;

public class Order implements Serializable {
    private String result = "";

    public Order() {}

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
