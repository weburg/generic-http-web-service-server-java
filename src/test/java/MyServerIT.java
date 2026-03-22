import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MyServerIT {
    @Test
    void myServerIT() throws IOException {
        HttpRequestBase request = new HttpGet("http://localhost:8081/generichttpws?ahttpi");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);

        String body = EntityUtils.toString(response.getEntity());
        assertTrue(body.contains("AHTTPI"));
    }
}