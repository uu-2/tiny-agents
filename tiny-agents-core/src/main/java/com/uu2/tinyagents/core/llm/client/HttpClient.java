
package com.uu2.tinyagents.core.llm.client;

import com.uu2.tinyagents.core.util.IOUtil;
import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient okHttpClient;

    public HttpClient() {
        this(OkHttpClientUtil.buildDefaultClient());
    }

    public HttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public String get(String url) {
        return executeString(url, "GET", null, null);
    }

    public byte[] getBytes(String url) {
        return executeBytes(url, "GET", null, null);
    }

    public String get(String url, Map<String, String> headers) {
        return executeString(url, "GET", headers, null);
    }

    public String post(String url, Map<String, String> headers, String payload) {
        return executeString(url, "POST", headers, payload);
    }

    public byte[] postBytes(String url, Map<String, String> headers, String payload) {
        return executeBytes(url, "POST", headers, payload);
    }

    public String put(String url, Map<String, String> headers, String payload) {
        return executeString(url, "PUT", headers, payload);
    }

    public String delete(String url, Map<String, String> headers, String payload) {
        return executeString(url, "DELETE", headers, payload);
    }

    public String multipartString(String url, Map<String, String> headers, Map<String, Object> payload) {
        try (Response response = multipart(url, headers, payload);
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.string();
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
        return null;
    }


    public byte[] multipartBytes(String url, Map<String, String> headers, Map<String, Object> payload) {
        try (Response response = multipart(url, headers, payload);
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.bytes();
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
        return null;
    }


    public Response multipart(String url, Map<String, String> headers, Map<String, Object> payload) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url);

        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }

        MultipartBody.Builder mbBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        payload.forEach((s, o) -> {
            if (o instanceof File) {
                File f = (File) o;
                RequestBody body = RequestBody.create(f, MediaType.parse("application/octet-stream"));
                mbBuilder.addFormDataPart(s, f.getName(), body);
            } else if (o instanceof InputStream) {
                RequestBody body = new InputStreamRequestBody(MediaType.parse("application/octet-stream"), (InputStream) o);
                mbBuilder.addFormDataPart(s, s, body);
            } else if (o instanceof byte[]) {
                mbBuilder.addFormDataPart(s, s, RequestBody.create((byte[]) o));
            } else {
                mbBuilder.addFormDataPart(s, String.valueOf(o));
            }
        });

        MultipartBody multipartBody = mbBuilder.build();
        Request request = builder.post(multipartBody).build();
        return okHttpClient.newCall(request).execute();
    }


    public String executeString(String url, String method, Map<String, String> headers, String payload) {
        try (Response response = execute0(url, method, headers, payload);
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.string();
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
        return null;
    }


    public byte[] executeBytes(String url, String method, Map<String, String> headers, String payload) {
        try (Response response = execute0(url, method, headers, payload);
             ResponseBody body = response.body()) {
            if (body != null) {
                return body.bytes();
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
        return null;
    }


    private Response execute0(String url, String method, Map<String, String> headers, String payload) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url);

        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }

        Request request;
        if ("GET".equalsIgnoreCase(method)) {
            request = builder.method(method, null).build();
        } else {
            RequestBody body = RequestBody.create(payload, JSON_TYPE);
            request = builder.method(method, body).build();
        }

        return okHttpClient.newCall(request).execute();
    }


    public static class InputStreamRequestBody extends RequestBody {
        private final InputStream inputStream;
        private final MediaType contentType;

        public InputStreamRequestBody(MediaType contentType, InputStream inputStream) {
            if (inputStream == null) throw new NullPointerException("inputStream == null");
            this.contentType = contentType;
            this.inputStream = inputStream;
        }

        @Override
        public MediaType contentType() {
            return contentType;
        }

        @Override
        public long contentLength() throws IOException {
            return inputStream.available() == 0 ? -1 : inputStream.available();
        }

        @Override
        public void writeTo(@NotNull BufferedSink sink) throws IOException {
            IOUtil.copy(inputStream, sink);
        }
    }
}
