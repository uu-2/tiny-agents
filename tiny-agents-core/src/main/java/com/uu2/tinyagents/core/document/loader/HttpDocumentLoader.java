
package com.uu2.tinyagents.core.document.loader;


import com.uu2.tinyagents.core.document.DocumentParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpDocumentLoader extends AbstractDocumentLoader {

    private String url;
    private Map<String, String> headers;

    private final OkHttpClient okHttpClient;


    public HttpDocumentLoader(DocumentParser documentParser, String url) {
        super(documentParser);
        this.url = url;
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build();
    }


    public HttpDocumentLoader(DocumentParser documentParser, String url, Map<String, String> headers) {
        super(documentParser);
        this.url = url;
        this.headers = headers;
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build();
    }


    @Override
    protected Map<String, Object> getMetadata() {
        return Map.of("origin", url);
    }

    @Override
    public InputStream loadInputStream() {
        Request.Builder builder = new Request.Builder()
                .url(url);

        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }

        // get method
        Request request = builder.get().build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body != null) {
                return body.byteStream();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
