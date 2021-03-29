package com.h2pl4u.spring.sourcecode.component;

import com.h2pl4u.spring.sourcecode.entity.BlueColor;
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
        PersonEntity lbw = (PersonEntity) applicationContext.getBean("lbw");
        System.out.println(jjf);
        System.out.println(lbw);
    }

    @Test
    public void BeanImportTest() {
        printBeans(applicationContext);
        BlueColor bean = applicationContext.getBean(BlueColor.class);
        System.out.println(bean);
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

    @Test
    public void BeanFactoryTest() {
        printBeans(applicationContext);
        //工厂bean获取的是调用getObject方法创建的对象
        Object bean1 = applicationContext.getBean("colorFactoryBean");
        Object bean2 = applicationContext.getBean("colorFactoryBean");
        Object bean3 = applicationContext.getBean("&colorFactoryBean");
        System.out.println(bean1 == bean2);
        System.out.println(bean3.getClass());
    }
}
