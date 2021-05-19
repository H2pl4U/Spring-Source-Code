package com.h2pl4u.spring.sourcecode.extension;

import com.h2pl4u.spring.sourcecode.entity.BlueColor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/5/19 16:01
 *
 * @Author Liuwei
 */
@Component
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        System.out.println("MyBeanDefinitionRegistryPostProcessor...bean数量:" + configurableListableBeanFactory.getBeanDefinitionCount());
    }

    /**
     * BeanDefinitionRegistry Bean定义信息的保存中心，以后BeanFactory就是按照BeanDefinitionRegistry
     *  BeanDefinitionRegistry里面保存的每一个bean定义信息，创建bean实例
     *
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("postProcessBeanDefinitionRegistry...bean数量:" + registry.getBeanDefinitionCount());
//        RootBeanDefinition beanDefinition = new RootBeanDefinition(BlueColor.class);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(BlueColor.class).getBeanDefinition();
        registry.registerBeanDefinition("hello", beanDefinition);
    }
}
