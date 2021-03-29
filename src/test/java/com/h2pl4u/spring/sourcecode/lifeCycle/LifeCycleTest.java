package com.h2pl4u.spring.sourcecode.lifeCycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created on 2021/3/29 10:14
 *
 * @Author Liuwei
 */

public class LifeCycleTest {

    @Test
    public void beanLifeCycleTest() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigLifeCycle.class);
        System.out.println("容器创建完成...");

        //关闭容器
        applicationContext.close();
    }

}
