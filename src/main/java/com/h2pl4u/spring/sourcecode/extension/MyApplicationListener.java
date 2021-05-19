package com.h2pl4u.spring.sourcecode.extension;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/5/19 16:33
 *
 * @Author Liuwei
 */
@Component
public class MyApplicationListener implements ApplicationListener<ApplicationEvent> {
    /**
     * 当容器中发布此事件以后，方法触发
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("收到事件：" + event);
    }
}
