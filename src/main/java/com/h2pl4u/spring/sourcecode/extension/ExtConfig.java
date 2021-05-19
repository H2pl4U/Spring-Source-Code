package com.h2pl4u.spring.sourcecode.extension;

/**
 * Created on 2021/5/17 14:27
 *
 * @Author Liuwei
 */

import com.h2pl4u.spring.sourcecode.entity.BlueColor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 扩展原理：
 * BeanPostProcessor：bean后置处理器，bean创建对象初始化前后进行拦截工作的
 * BeanFactoryPostProcessor：beanFactory的后置处理器
 *      在BeanFactory标准初始化之后调用，所有的bean定义已经保存加载到beanFactory中，但是bean的实例还未创建
 *
 * 1.invokeBeanFactoryPostProcessors(beanFactory); 执行BeanFactoryPostProcessor
 *      如何找到所有的BeanFactoryPostProcessor 并执行他们的方法
 *          1】直接在BeanFactory中找到所有类型是 BeanFactoryPostProcessor的组件，并执行他们的组件
 *          2】在初始化创建其他前面执行
 * 2. interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor
 *      postProcessBeanDefinitionRegistry()
 *      在所有bean定义信息将要被加载，bean实例还未创建的
 *      优先于BeanFactoryPostProcessor执行，可以利用BeanDefinitionRegistryPostProcessor给容器中再额外添加一些组件
 * 原理：
 *      1.ioc创建对象
 *      2.refresh() -> invokeBeanFactoryPostProcessors(beanFactory);
 *      3.从容器中获取到所有的BeanDefinitionRegistryPostProcessor组件
 *          1】依次触发所有的postProcessBeanDefinitionRegistry()方法
 *          2】再来触发postProcessBeanFactory()方法BeanFactoryPostProcessor
 *      4.再来从容器中找到BeanFactoryPostProcessor组件:然后依次触发postProcessBeanFactory()方法
 *
 * 3.ApplicationListener：监听容器中发布的事件，完成事件驱动模型开发
 *      public interface ApplicationListener<E extends ApplicationEvent>
 *      监听 ApplicationEvent 及其下面的子事件
 *
 * 步骤：
 *      1.写一个监听器(ApplicationListener实现类)来监听某个事件(ApplicationEvent及其子类)
 *          或者使用 @EventListener注释 同样可以实现监听某个事件
 *         【原理】使用EventListenerMethodProcessor处理器来解析方法上的@EventListener注解
 *      2.把监听器加入到容器
 *      3.只要容器中有相关事件发布，就能监听到此事件
 *          ContextRefreshedEvent 容器刷新完成(所有bean都完全创建)会发布这个事件
 *          ContextClosedEvent 关闭容器会发布这个事件
 *      4.自定义事件发布
 *
 * 原理：
 *      ContextRefreshedEvent、ExtTest$1[source=myApplicationEvent]、ContextClosedEvent
 *      1、ContextRefreshedEvent事件
 *          1】容器创建对象：refresh()
 *          2】finishRefresh();容器刷新完成会发布ContextRefreshedEvent事件
 *      2.自定义事件
 *      3.容器关闭会发布ContextClosedEvent事件
 *
 *      [事件发布流程]：
 *          3】publishEvent(new ContextRefreshedEvent(this));
 *              1)获取事件的多播器(派发器):getApplicationEventMulticaster()
 *              2)multicastEvent派发事件
 *              3)获取到所有的ApplicationListener(循环遍历)
 *                  ①如果有Executor，可以支持使用Executor进行异步派发
 *                      Executor executor = getTaskExecutor();
 *                  ②否则，同步的方式直接执行listener方法
 *                      invokeListener(event);拿到listener回调onApplicationEvent()方法
 *     [事件多播器(派发器)]
 *          1.容器创建对象：refresh();
 *          2.initApplicationEventMulticaster();初始化ApplicationEventMulticaster；
 *              1】先去容器中找有没有id="applicationEventMulticaster"的组件
 *              2】如果没有，new SimpleApplicationEventMulticaster()
 *                  并且加入到容器中，就可以再其他组件要派发事件时自动注入此applicationEventMulticaster
 *     [容器中有哪些监听器]
 *          1.容器创建对象：refresh();
 *          2.registerListeners();
 *              从容器中拿到所有的监听器并将他们都注册到applicationEventMulticaster中
 *              String[] listenerBeanNames = this.getBeanNamesForType(ApplicationListener.class, true, false);
 *              将listener注册到ApplicationEventMulticaster中
 *              this.getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
 *
 *   [SmartInitializingSingleton原理]
 *      1.ioc容器创建对象,并refresh()刷新容器
 *      2.this.finishBeanFactoryInitialization(beanFactory);初始化剩下的单实例bean
 *          1】先创建所有的单实例bean，getBean()；
 *          2】获取所有创建好的单实例bean，判断是否为SmartInitializingSingleton
 *              如果是则调用afterSingletonsInstantiated();
 *
 */
@ComponentScan("com.h2pl4u.spring.sourcecode.extension")
@Configuration
public class ExtConfig {

    @Bean
    public BlueColor blueColor() {
        return new BlueColor();
    }
}
