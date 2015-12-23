package eu.fusepool.p3.transformer;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wrapping a simple Http request
 */
public class HttpClientWrapper {

    public String executeRequest(String url) throws IOException {
        String result = "";
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {
            InputStream inputStream = responseEntity.getContent();
            try {
                result = IOUtils.toString(inputStream, "UTF-8");
            } finally {
                //TODO
            }
        }
        return result;
    }
}
