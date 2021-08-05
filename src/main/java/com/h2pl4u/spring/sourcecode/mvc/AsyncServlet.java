package com.h2pl4u.spring.sourcecode.mvc;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 异步处理
 */
@WebServlet(value = "/async", asyncSupported = true)
public class AsyncServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.支持异步处理asyncSupported = true
        //2.开启异步模式
        System.out.println(Thread.currentThread() + "主线程开始..." + System.currentTimeMillis());
        AsyncContext asyncContext = req.startAsync();
        //3.业务逻辑进行异步处理
        asyncContext.start(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread() + "副线程开始..." + System.currentTimeMillis());
                    hello();
                    asyncContext.complete();
                    //获取异步上下文
                    AsyncContext context = req.getAsyncContext();
                    ServletResponse response = asyncContext.getResponse();
                    response.getWriter().write("hello async");
                    System.out.println(Thread.currentThread() + "副线程结束..." + System.currentTimeMillis());
                } catch (Exception e) {
                }
            }
        });
        System.out.println(Thread.currentThread() + "主线程结束..." + System.currentTimeMillis());
        super.doGet(req, resp);
    }

    public void hello() throws InterruptedException {
        System.out.println(Thread.currentThread() + "processing...");
        TimeUnit.SECONDS.sleep(10);
    }
}
