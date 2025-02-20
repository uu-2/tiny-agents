
package com.uu2.tinyagents.core.image.transfer;


import com.uu2.tinyagents.core.image.Image;
import com.uu2.tinyagents.core.util.Metadata;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@SuperBuilder
public class ImageResponse extends Metadata {
    private List<Image> images;
    private boolean error;
    private String errorMessage;


    public static ImageResponse error(String errMessage) {
        return ImageResponse.builder()
                .error(true)
                .errorMessage(errMessage)
                .build();
    }


    public void addImage(String url) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }

        this.images.add(Image.ofUrl(url));
    }

    public void addImage(byte[] bytes) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }

        this.images.add(Image.ofBytes(bytes));
    }

    @Override
    public String toString() {
        return "ImageResponse{" +
            "images=" + images +
            ", error=" + error +
            ", errorMessage='" + errorMessage + '\'' +
            ", metadataMap=" + metadataMap +
            '}';
    }
}
