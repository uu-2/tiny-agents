
package com.uu2.tinyagents.core.llm.client;


import com.uu2.tinyagents.core.util.StringUtil;
import lombok.Getter;
import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class OkHttpClientUtil {

    @Getter
    private static OkHttpClient.Builder okHttpClientBuilder;

    public static void setOkHttpClientBuilder(OkHttpClient.Builder okHttpClientBuilder) {
        OkHttpClientUtil.okHttpClientBuilder = okHttpClientBuilder;
    }

    public static OkHttpClient buildDefaultClient() {
        if (okHttpClientBuilder != null) {
            return okHttpClientBuilder.build();
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES);

        String proxyHost = System.getProperty("https.proxyHost");
        String proxyPort = System.getProperty("https.proxyPort");

        if (StringUtil.hasText(proxyHost) && StringUtil.hasText(proxyPort)) {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(proxyHost.trim(), Integer.parseInt(proxyPort.trim()));
            builder.proxy(new Proxy(Proxy.Type.HTTP, inetSocketAddress));
        }

        return builder.build();
    }
}
