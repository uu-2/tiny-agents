package com.uu2.tinyagents.core.rag.preretrieval;

import com.uu2.tinyagents.core.rag.Question;

public interface PreRetrieval {

    Question invoke(Question query);
}
