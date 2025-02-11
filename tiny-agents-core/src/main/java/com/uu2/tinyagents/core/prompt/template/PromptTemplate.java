
package com.uu2.tinyagents.core.prompt.template;

import java.util.Map;

public interface PromptTemplate<R> {

     R format(Map<String, Object> params);

}
