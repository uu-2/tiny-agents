
package com.uu2.tinyagents.core.util;

import com.uu2.tinyagents.core.llm.client.HttpClient;

import java.io.File;
import java.io.InputStream;
import java.util.Base64;

public class ImageUtil {

    private static final HttpClient imageHttpClient = new HttpClient();


    public static String imageUrlToBase64(String imageUrl) {
        byte[] bytes = imageHttpClient.getBytes(imageUrl);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String imageFileToBase64(File imageFile) {
        byte[] bytes = IOUtil.readBytes(imageFile);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String imageStreamToBase64(InputStream imageStream) {
        byte[] bytes = IOUtil.readBytes(imageStream);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
