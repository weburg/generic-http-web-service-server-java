package viewbeans;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

public class About implements Serializable {
    private ZonedDateTime date = ZonedDateTime.now();
    private String requestUri = "";
    private String myFunctionOutput = "";

    public About() {}

    public Date getDate() {
        // Note: Instant.now gives an Instant, and its .toString() formats to ISO 8601 UTC.
        return Date.from(date.toInstant());
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getMyFunctionOutput() {
        return myFunctionOutput;
    }

    public void setMyFunctionOutput(String myFunctionOutput) {
        this.myFunctionOutput = myFunctionOutput;
    }
}
