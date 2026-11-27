package viewbeans;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Orders implements Serializable {
    private Map<String, Object> orders = new LinkedHashMap<>();

    public Orders() {}

    public Map<String, Object> getOrders() {
        return orders;
    }

    public void setOrders(Map<String, Object> order) {
        this.orders = order;
    }
}
