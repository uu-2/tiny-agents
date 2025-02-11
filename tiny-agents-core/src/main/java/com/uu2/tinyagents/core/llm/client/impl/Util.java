
package com.uu2.tinyagents.core.llm.client.impl;

import com.uu2.tinyagents.core.llm.exception.LlmException;
import com.uu2.tinyagents.core.util.StringUtil;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

class Util {

    public static Throwable getFailureThrowable(Throwable t, Response response) {
        if (t != null) {
            return t;
        }

        if (response != null) {
            String errMessage = "Response code: " + response.code();
            String message = response.message();
            if (StringUtil.hasText(message)) {
                errMessage += ", message: " + message;
            }
            try (ResponseBody body = response.body()) {
                if (body != null) {
                    String string = body.string();
                    if (StringUtil.hasText(string)) {
                        errMessage += ", body: " + string;
                    }
                }
            } catch (IOException e) {
                // ignore
            }
            t = new LlmException(errMessage);
        }

        return t;

    }
}
