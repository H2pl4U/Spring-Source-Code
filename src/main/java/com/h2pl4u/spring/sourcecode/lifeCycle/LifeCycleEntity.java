package com.h2pl4u.spring.sourcecode.lifeCycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/3/29 10:29
 *
 * @Author Liuwei
 */
@Component
public class LifeCycleEntity implements InitializingBean, DisposableBean {
    public LifeCycleEntity() {
        System.out.println("LifeCycleEntity construction");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("DisposableBean destroy...");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("InitializingBean afterPropertiesSet...");
    }
}
