
package com.uu2.tinyagents.core.image;

import com.uu2.tinyagents.core.llm.client.HttpClient;
import com.uu2.tinyagents.core.util.IOUtil;
import com.uu2.tinyagents.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Arrays;
import java.util.Base64;

@Setter
@Getter
public class Image {

    /**
     * The base64-encoded JSON of the generated image
     */
    private String b64Json;

    /**
     * The URL of the generated image
     */
    private String url;

    /**
     * The data of image
     */
    private byte[] bytes;

    public static Image ofUrl(String url) {
        Image image = new Image();
        image.setUrl(url);
        return image;
    }

    public static Image ofBytes(byte[] bytes) {
        Image image = new Image();
        image.setBytes(bytes);
        return image;
    }

    public byte[] readBytes() {
        return bytes;
    }

    public void writeToFile(File file) {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IllegalStateException("Can not mkdirs for path: " + file.getParentFile().getAbsolutePath());
        }
        if (this.bytes != null && this.bytes.length > 0) {
            IOUtil.writeBytes(this.bytes, file);
        } else if (this.b64Json != null) {
            byte[] bytes = Base64.getDecoder().decode(b64Json);
            IOUtil.writeBytes(bytes, file);
        } else if (StringUtil.hasText(this.url)) {
            byte[] bytes = new HttpClient().getBytes(this.url);
            IOUtil.writeBytes(bytes, file);
        }
    }

    @Override
    public String toString() {
        return "Image{" +
            "b64Json='" + b64Json + '\'' +
            ", url='" + url + '\'' +
            ", bytes=" + Arrays.toString(bytes) +
            '}';
    }
}
