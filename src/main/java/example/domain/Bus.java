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

    //public Engine getEngine() {
    //    return engine;
    //}

    //public void setEngine(Engine engine) {
    //    this.engine = engine;
    //}
}