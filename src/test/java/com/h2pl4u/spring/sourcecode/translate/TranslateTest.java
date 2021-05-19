package com.h2pl4u.spring.sourcecode.translate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created on 2021/5/17 13:27
 *
 * @Author Liuwei
 */
@SpringBootTest
public class TranslateTest {

    @Test
    public void userTest() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TranslateConfig.class);
        TranslateService translateService = applicationContext.getBean(TranslateService.class);
        translateService.insert();
        applicationContext.close();
    }
}
