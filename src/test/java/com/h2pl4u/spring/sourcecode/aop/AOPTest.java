package com.h2pl4u.spring.sourcecode.aop;

import com.h2pl4u.spring.sourcecode.component.MainConfigOfAop;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootTest
public class AOPTest {

    @Test
    public void aopTest() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfAop.class);
        //自定义的对象调用div方法无法使用aop
//        MathCalculator mathCalculator = new MathCalculator();
//        mathCalculator.div(10, 5);
        //使用spring容器中的组件
        MathCalculator calculator = applicationContext.getBean(MathCalculator.class);
        calculator.div(10, 2);
        applicationContext.close();

    }
}
