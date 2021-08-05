package com.h2pl4u.spring.sourcecode.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * SpringMVC异步处理
 */
@Controller
public class AsyncController {

    @ResponseBody
    @RequestMapping("/createOrder")
    public DeferredResult<Object> createOrder() {
        DeferredResult<Object> deferredResult = new DeferredResult<>(3000L, "create fail");
        return deferredResult;
    }

    @ResponseBody
    @RequestMapping("/create")
    public String create() {
        //创建订单
        String order = UUID.randomUUID().toString();
        DeferredResult<Object> deferredResult = DeferredResultQueue.get();
        deferredResult.setResult(order);
        return "success=====>" + order;
    }

    /**
     * 1.控制器返回Callable
     * 2.Spring异步处理，将Callable提交TaskExecutor 使用一个隔离的线程进行执行
     * 3.DispatcherServlet和所有的Filter推出web容器的线程，但是response保持打开状态
     * 4.Callable返回结果，SpringMVC将请求重新派发给容器，恢复之前的处理
     * 5.根据Callable返回的结果，SpringMVC继续进行视图渲染流程等(从请求到视图渲染)
     *
     * 异步拦截器
     * 1.原始API的AsyncListener
     * 2.SpringMVC：实现AsyncHandlerInterceptor
     * @return
     */
    @ResponseBody
    @RequestMapping("/async01")
    public Callable<String> async01() {
        System.out.println(Thread.currentThread() + "主线程开始..." + System.currentTimeMillis());
        Callable<String> callable = new Callable<String>() {

            @Override
            public String call() throws Exception {
                System.out.println(Thread.currentThread() + "副线程开始..." + System.currentTimeMillis());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread() + "副线程结束..." + System.currentTimeMillis());
                return "Callable<String> async01()";
            }
        };
        System.out.println(Thread.currentThread() + "主线程结束..." + System.currentTimeMillis());
        return callable;
    }
}
