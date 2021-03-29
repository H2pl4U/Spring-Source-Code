package com.h2pl4u.spring.sourcecode.propertyValue;

import com.h2pl4u.spring.sourcecode.entity.PropertyEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created on 2021/3/29 14:59
 *
 * @Author Liuwei
 */
@SpringBootTest
public class PropertyValueTest {

    @Autowired
    private PropertyEntity propertyEntity;

    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig4PropertyValues.class);
        printBeans(applicationContext);
        PropertyEntity property = (PropertyEntity)applicationContext.getBean("property");
        System.out.println(property);
        applicationContext.close();
        System.out.println(propertyEntity);
    }

    private void printBeans(AnnotationConfigApplicationContext applicationContext) {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            System.out.println(name);
        }
    }
}
