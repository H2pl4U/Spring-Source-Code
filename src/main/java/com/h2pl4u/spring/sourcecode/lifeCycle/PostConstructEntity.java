package com.h2pl4u.spring.sourcecode.entity;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created on 2021/3/29 10:38
 *
 * @Author Liuwei
 */
@Component
public class PostConstructEntity {
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
     * 容器清理对象之前调用
     */
    @PreDestroy
    public void destory() {
        System.out.println("@PreDestroy...");
    }
}
