package client;

import com.amazonaws.util.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;

public class HttpClient {


    public static String returnJsonFromEndpoint(String endpointURL) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(endpointURL);
            httpGet.setHeader("Content-type", "application/json; charset=UTF-8");
            try (CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpGet)) {
                int status = closeableHttpResponse.getStatusLine().getStatusCode();
                if (status == 200) {
                    InputStream inputStream = closeableHttpResponse.getEntity().getContent();
                    return IOUtils.toString(inputStream);
                } else {
                    throw new RuntimeException(
                            "Error doing GET. Received status code "
                                    + status
                                    + " and message "
                                    + closeableHttpResponse.getStatusLine().getReasonPhrase());
                }
            }
        }
    }
}
