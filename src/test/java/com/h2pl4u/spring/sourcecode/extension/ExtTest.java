package com.h2pl4u.spring.sourcecode.extension;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created on 2021/5/17 14:32
 *
 * @Author Liuwei
 */
@SpringBootTest
public class ExtTest {
    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ExtConfig.class);

        applicationContext.close();
    }

}
