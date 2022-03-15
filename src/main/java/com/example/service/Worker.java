package com.example.service;

import javax.servlet.AsyncContext;
import java.io.IOException;

public class Worker {
    private AsyncContext asyncContext;
    private long createTime;

    public Worker(AsyncContext asyncContext) {
        asyncContext.setTimeout(10000);
        this.asyncContext = asyncContext;
        createTime = System.currentTimeMillis();
    }

    public boolean isExpire(int mill) {
        return System.currentTimeMillis() - createTime > mill;
    }

    public AsyncContext getAsyncContext() {
        return asyncContext;
    }

    public void setAsyncContext(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
    }

    public void response(String responseBody) throws IOException {
        asyncContext.getResponse().getWriter().write(responseBody);
        asyncContext.complete();
    }
}
