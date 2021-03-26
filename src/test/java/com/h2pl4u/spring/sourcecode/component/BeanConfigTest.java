package com.h2pl4u.spring.sourcecode.component;

import com.h2pl4u.spring.sourcecode.entity.PersonEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created on 2021/3/26 13:57
 *
 * @Author Liuwei
 */
@SpringBootTest
public class BeanConfigTest {

    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfig.class);

    @Test
    public void BeanTest() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanConfig.class);
        PersonEntity jjf = (PersonEntity) applicationContext.getBean("jjf");
        PersonEntity jbh = (PersonEntity) applicationContext.getBean("jbh");
        System.out.println(jjf);
        System.out.println(jbh);
    }

    @Test
    public void BeanImportTest() {
        printBeans(applicationContext);
    }



    @Test
    public void ComponentScanTest() {
        printBeans(applicationContext);
    }

    private void printBeans(AnnotationConfigApplicationContext applicationContext) {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            System.out.println(name);
        }
    }
}
