package example.domain;

public abstract class Bus {
    private String callsign = null;

    //private Engine engine = null;
    public String color = "yellow";

    public Bus(String callsign) {
        this.callsign = callsign;
        //this.engine = new Engine();
        //this.engine.setCylinders(8);
        //this.engine.setName("Bluebird V8");
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    //public Engine getEngine() {
    //    return engine;
    //}

    //public void setEngine(Engine engine) {
    //    this.engine = engine;
    //}
}