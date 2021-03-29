package com.h2pl4u.spring.sourcecode.lifeCycle;

import com.h2pl4u.spring.sourcecode.entity.PersonEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * bean的生命周期：
 *      bean创建---初始化---销毁
 * 容器管理bean的生命周期
 * 构造(创建对象)
 *      单实例：容器启动时创建对象
 *      多实例：每次获取时创建对象
 *
 * 初始化:
 *      对象创建完成并赋值完成，调用初始化方法
 * 销毁:
 *      单实例：容器关闭时销毁
 *      多实例：容器不会管理当前bean，容器关闭时不会调用销毁方法
 *
 * 遍历得到容器中所有的BeanPostProcessor:依次执行beforeInitialization，
 * 一旦返回null，跳出for循环，不会执行后面的BeanPostProcessor.postProcessorsBeforeInitialization
 *
 * BeanPostProcessor原理:
 * 1.populateBean(beanName, mbd, instanceWrapper); //给bean进行属性赋值
 * 2.initializeBean(beanName, mbd, instanceWrapper) {
 *     applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
 *     invokeInitMethods(String beanName, Object bean, @Nullable RootBeanDefinition mbd)
 *     applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
 * }
 *
 * 可以自定义初始化和销毁方法：容器在bean进行到当前生命周期时来调用自定义初始化和销毁
 * 1).指定初始化和销毁方法：在实体类中定义init-method和destroy-method并在@Bean中指定
 * 2).通过让Bean实现InitializingBean(定义初始化逻辑)和DisposableBean(定义销毁逻辑)
 * 3).使用JRS250规范中的@PostConstruct(在bean创建完成并且属性赋值完成后执行初始化方法)和PreDestroy(在容器销毁bean之前进行清理工作)
 * 4).BeanPostProcessor接口[bean的后置处理器],postProcessBeforeInitialization(在初始化工作之前)和postProcessAfterInitialization(在初始化工作之后)
 *
 * Spring底层对 BeanPostProcessor 的使用：
 *      bean赋值，注入其他组件，@Autowired，生命周期注解功能，@Async，xxxBeanPostProcessor;
 *
 * Created on 2021/3/29 9:59
 *
 * @Author Liuwei
 */
@ComponentScan("com.h2pl4u.spring.sourcecode.lifeCycle")
@Configuration
public class MainConfigLifeCycle {

//    @Scope("prototype")
    @Bean(value = "lbw01", initMethod = "initMethod", destroyMethod = "destroyMethod")
    public PersonEntity person01() {
        return new PersonEntity(1, "lbw", 22, false, "12345678912");
    }

    @Bean(value = "lbw02", initMethod = "initMethod", destroyMethod = "destroyMethod")
    public PersonEntity person02() {
        return new PersonEntity(1, "lbw", 22, false, "12345678912");
    }
}
