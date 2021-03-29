package com.h2pl4u.spring.sourcecode.lifeCycle;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created on 2021/3/29 10:38
 *
 * @Author Liuwei
 */
@Component
public class PostConstructEntity implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public PostConstructEntity() {
        System.out.println("postConstruct constructor...");
    }

    /**
     * 对象创建并赋值完成后调用
     */
    @PostConstruct
    public void init() {
        System.out.println("@PostConstruct...");
    }

    /**
     * 容器移除对象之前调用
     */
    @PreDestroy
    public void destory() {
        System.out.println("@PreDestroy...");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
