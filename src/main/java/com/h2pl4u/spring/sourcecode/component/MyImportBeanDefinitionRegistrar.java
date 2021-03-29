package com.h2pl4u.spring.sourcecode.component;

import com.h2pl4u.spring.sourcecode.entity.RainBow;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Created on 2021/3/29 9:34
 *
 * @Author Liuwei
 */
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * 把所有需要添加到容器中的bean
     * BeanDefinitionRegistry.registerBeanDefinition手动注册进来
     *
     * @param importingClassMetadata 当前类的注解信息
     * @param registry               BeanDefinition注册类
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition("com.h2pl4u.spring.sourcecode.entity.BlueColor")
                && registry.containsBeanDefinition("com.h2pl4u.spring.sourcecode.entity.RedColor")) {
            //指定Bean定义信息(bean的类型，作用域等)
            RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(RainBow.class);
            //注册一个Bean
            registry.registerBeanDefinition("rainBow", rootBeanDefinition);
        }
    }
}
