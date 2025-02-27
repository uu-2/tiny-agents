package com.uu2.tinyagents.core.rag;

import com.uu2.tinyagents.core.document.Document;
import com.uu2.tinyagents.core.document.store.Store;
import com.uu2.tinyagents.core.util.Metadata;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class Question extends Metadata {
    private String text;
    private List<String> queryTranslation;
    private List<Store> stores;
    private List<Document> documents;
    private String promptTemplate;

    public static Question of(String query) {
        return Question.builder()
                .text(query)
                .build();
    }
}
