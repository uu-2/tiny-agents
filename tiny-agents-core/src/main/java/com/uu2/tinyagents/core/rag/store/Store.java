package com.uu2.tinyagents.core.rag.store;

import java.util.List;

public interface Store {
    List<Document> search(Query query);
}
