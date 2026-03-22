package beans;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

public class AboutBean implements Serializable {
    private ZonedDateTime date = ZonedDateTime.now();
    private String requestUri = "";

    public AboutBean() {}

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
}
