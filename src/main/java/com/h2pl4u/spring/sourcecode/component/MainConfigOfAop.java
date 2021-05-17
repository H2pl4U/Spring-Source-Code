package com.h2pl4u.spring.sourcecode.component;

import com.h2pl4u.spring.sourcecode.aop.LogAspects;
import com.h2pl4u.spring.sourcecode.aop.MathCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP(面向切面编程)
 *  在程序中运行期间动态的将某段代码切入到指定方法指定位置进行运行的编程方式
 *  1、导入AOP模块，Spring AOP (spring-aspects)
 *  2、定义业务逻辑类(MathCalculator)，在业务逻辑运行的时候将日志进行打印（方法之前，方法运行结束，方法出现异常）
 *  3、定义日志切面类(LogAspects)，切面类里面的方法需要动态感知MathCalculator.div()运行过程
 *          通知方法:
 *              前置通知(@Before): logStart在目标方法(div)运行之前执行
 *              后置通知(@After): logEnd在目标方法(div)运行结束之后执行
 *              返回通知(@AfterReturning): logReturn在目标方法(div)正常返回之后执行
 *              异常通知(@AfterThrowing): logException在目标方法(div)运行出现异常后执行
 *              环绕通知(@Round): 动态代理，手动推荐目标方法运行(joinPoint.proceed())
 *  4、给切面类的目标方法标注运行时刻与位置(标注通知注解)
 *  5、将切面类和业务逻辑类(目标所在类)都加入到容器中
 *  6、必须告诉Spring哪个类是切面类(给切面类上添加@Aspect注解)
 *  7、需要给配置类中添加@EnableAspectJAutoProxy来开启基于注解的aop模式(以往的xml中添加<aop:aspectj-autoproxy></aop:aspectj-autoproxy>)
 *      在Spring中有很多的@Enablexxx
 *
 *  AOP核心三步骤：
 *  1)、将业务逻辑组件和切面类都加入到容器中，告诉spring哪个是切面类(@Aspect)
 *  2)、在切面类上的每个通知方法上标注通知注解，告诉spring何时何地运行(切入点表达式)
 *  3)、开启基于注解的AOP模式，@EnableAspectJAutoProxy
 *
 *  AOP原理【分析思想：给容器中注册了什么组件，组件什么时候工作以及作用】：
 *      @EnableAspectJAutoProxy
 *  1.@EnableAspectJAutoProxy是什么？
 *      @Import({AspectJAutoProxyRegistrar.class})，给容器中导入AspectJAutoProxyRegistrar类
 *          利用AspectJAutoProxyRegistrar自定义给容器中注册bean(BeanDefinition)
 *          internalAutoProxyCreator=AnnotationAwareAspectJAutoProxyCreator
 *      给容器中注册一个AnnotationAwareAspectJAutoProxyCreator；
 *
 *  2.AnnotationAwareAspectJAutoProxyCreator
 *      AnnotationAwareAspectJAutoProxyCreator
 *          -> AspectJAwareAdvisorAutoProxyCreator
 *              -> AbstractAdvisorAutoProxyCreator
 *                  -> AbstractAutoProxyCreator implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware
 *                  重点关注后置处理器(在bean初始化完成前后处理事情)、自动装配BeanFactory
 *      AbstractAutoProxyCreator.setBeanFactory()
 *      AbstractAutoProxyCreator.postProcessAfterXXX()【后置处理器方法】
 *
 *      AbstractAdvisorAutoProxyCreator.setBeanFactory() 重写此方法，并在方法中调用了initBeanFactory()
 *
 *      AnnotationAwareAspectJAutoProxyCreator.initBeanFactory() 由父类调用
 *
 *  3.调式调用流程：
 *      1）传入配置类，创建ioc容器
 *      2）注册配置类，调用refresh()刷新容器
 *      3）registerBeanPostProcessors(beanFactory) 注册bean的后置处理器来方便拦截bean的创建；
 *          1】先获取ioc容器已经定义了的需要创建对象的所有BeanPostProcessor
 *          2】给容器中添加别的BeanPostProcessor
 *          3】优先注册实现了PriorityOrdered接口的BeanPostProcessor
 *          4】再给容器中注册实现了Ordered接口的BeanPostProcessor
 *          5】注册没实现优先级接口的BeanPostProcessor
 *          6】注册BeanPostProcessor，实际上就是创建注册BeanPostProcessor对象，保存在容器中：
 *              创建internalAutoProxyCreator的BeanPostProcessor【AnnotationAwareAspectJAutoProxyCreator】
 *              1]创建Bean示例
 *              2]populateBean，给bean各种属性赋值
 *              3]initializeBean：初始化bean
 *                  ①invokeAwareMethods() 处理Aware接口的方法回调
 *                  ②applyBeanPostProcessorsBeforeInitialization():应用后置处理器的postProcessBeforeInitialization()
 *                  ③invokeInitMethods()执行自定义的初始化方法
 *                  ④applyBeanPostProcessorsAfterInitialization():执行后置处理器的postProcessorsAfterInitialization()
 *              4]BeanPostProcessor(AnnotationAwareAspectJAutoProxyCreator)创建成功：--》aspectJAdvisorsBuilder
 *          7】把BeanPostProcessor注册BeanFactory中:
 *              beanFactory.addBeanPostProcessor(postProcessor);
 *
 *   =====================以上是创建和注册AnnotationAwareAspectJAutoProxyCreator的过程=====================
 *      AnnotationAwareAspectJAutoProxyCreator => InstantiationAwareBeanPostProcessor
 *   4.finishBeanFactoryInitialization(beanFactory);完成BeanFactory初始化工作，创建剩下的单实例Bean
 *      1）遍历获取容器中所有的Bean，依次创建对象[getBean(beanName)];
 *          getBean() -> doGetBean() -> getSingleton()
 *      2）创建Bean
 *          【AnnotationAwareAspectJAutoProxyCreator在所有bean创建之前会有一个拦截，InstantiationAwareBeanPostProcessor，会调用postProcessBeforeInstantiation()】
 *          1】先从缓存中获取当前bean，如果能获取到说明bean在之前创建过可以直接使用，否则再创建
 *              只要创建好的Bean都会被缓存起来
 *          2】createBean() 创建bean (AnnotationAwareAspectJAutoProxyCreator会在任何bean创建之前先尝试返回Bean实例)
 *              【BeanPostProcessor是在Bean对象创建完成初始化前后调用的】
 *              【InstantiationAwareBeanPostProcessor是在创建Bean实例之前先尝试用后置处理器返回对象的】
 *              ①resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd)解析BeforeInstantiation
 *                  希望后置处理器再此能返回一个代理对象，如果能返回代理对象就使用，如果不能就继续
 *                  后置处理器先尝试返回对象:
 *                  bean = applyBeanPostProcessorsBeforeInstantiation()
 *                      拿到所有后置处理器，如果是InstantiationAwareBeanPostProcessor，则执行postProcessBeforeInstantiation
 *                  if (bean != null) {
 *                      bean = applyBeanPostProcessorsAfterInitialization(bean, beanName)
 *                  }
 *              ②doCreateBean(beanName, mbdToUse, args) 真正的去创建也给bean实例，和3.6流程一样
 *
 *  AnnotationAwareAspectJAutoProxyCreator【InstantiationAwareBeanPostProcessor】的作用:
 *  1)每一个bean创建之前调用postProcessBeforeInstantiation()
 *      关注MathCalculator和LogAspect的创建
 *      1】判断当前bean是否在advisedBeans中(保存了所有需要增强的bean)
 *      2】判断当前bean是否是基础类型的Advice、Pointcut、Advisor、AopInfrastructureBean，或者是否是切面(@Aspect)
 *      3】是否需要跳过bean处理
 *          ①获取候选的增强器(切面里面的通知方法)【List<Advisor> candidateAdvisors】
 *              每一个封装的通知方法的增强器是InstantiationModelAwarePointcutAdvisor
 *              判断每一个增强器是否是AspectJPointcutAdvisor类型的，返回true
 *          ②永远返回false
 *  2)创建对象
 *  postProcessAfterInitialization：
 *      return warpIfNecessary(bean, beanName, cacheKey); //包装如果需要的情况下
 *      1】获取当前bean的所有增强器(通知方法) 返回 Object[] specificInterceptors
 *          ①找到候选所有的增强器(找哪些通知方法是需要切人当前bean方法)
 *          ②获取到能在bean使用的增强器
 *          ③给增强器排序
 *      2】保存当前bean在advisedBeans中
 *      3】如果当前bean需要增强，创建当前bean的代理对象
 *          ①获取所有增强器(通知方法)
 *          ②保存到proxyFactory中
 *          ③创建代理对象，JdkDynamicAopProxy(config) jdk动态代理 或者 ObjenesisCglibAopProxy(config) cglib动态代理
 *      4】给容器中返回当前组件使用cglib增强了的代理对象
 *      5】以后容器中获取到的就是这个组件的代理对象，执行目标方法的时候，代理对象就会执行通知方法的流程
 * 3)目标方法执行
 *      容器中保存了组件的代理对象(cglib增强后的对象)，这个对象里面保存了详细信息(比如增强器，目标对象等)
 *      1】CglibAopProxy.intercept()拦截目标方法的执行
 *      2】根据ProxyFactory对象获取目标方法将要执行的目标方法拦截器链
 *          List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice
 *          ①创建List<Object> interceptorList保存所有的拦截器(共五个)
 *              一个默认的ExposeInvocationInterceptor和4个增强器
 *          ②遍历所有的增强器，将其转为interceptor
 *              registry.getInterceptors(advisor);
 *          ③将增强器转为List<MethodInterceptor>
 *              如果是MethodInterceptor则直接加入到集合中
 *              如果不是，使用AdvisorAdaptor将增强器转为MethodInterceptor
 *              转换完成返回MethodInterceptor数组
 *      3】如果没有拦截器链，直接执行目标方法
 *          拦截器链(每一个通知方法又被包住为拦截器，利用MethodInterceptor机制)
 *      4】如果由拦截器链，把需要执行的目标对象，目标方法
 *          拦截器链等信息传入创建一个CglibMethodInvocation对象
 *          并调用其 Object retVal = mi.proceed()
 *      5】拦截器链的触发过程
 *          ①如果没有拦截器执行目标方法，或者拦截器的索引数组-1大小不一样(指定到了最后一个拦截器)执行目标方法
 *          ②链式获取每一个拦截器，拦截器执行invoke方法，每一个拦截器等待下一个拦截器执行完成返回以后再执行
 *              拦截器链的机制，保证通知方法与目标方法的执行顺序
 *
 * 总结：
 *      1）@EnableAspectJAutoProxy 开启AOP功能
 *      2）@EnableAspectJAutoProxy 会给容器中注册一个组件AnnotationAwareAspectJAutoProxyCreator
 *      3）AnnotationAwareAspectJAutoProxyCreator 是一个后置处理器
 *      4）容器创建过程
 *          1】registerBeanPostProcessors() 注册后置处理器，创建AnnotationAwareAspectJAutoProxyCreator实例
 *          2】finishBeanFactoryInitialization() 初始化剩下的单实例bean
 *              ①创建业务逻辑组件和切面组件
 *              ②AnnotationAwareAspectJAutoProxyCreator拦截组件的创建过程
 *              ③组件创建完成后，判断组件是否需要增强
 *                  是:切面的通知方法，包装成增强器(Advisor)，给业务逻辑组件创建一个代理对象(cglib)
 *      5）执行目标方法
 *          1】代理对象执行目标方法
 *          2】CglibAopProxy.intercept()
 *              ①得到目标方法的拦截器链(增强器包装拦截器MethodInterceptor)
 *              ②利用拦截器的链式机制，依次进入每一个拦截器进行执行
 *              ③效果：
 *                  正常执行：前置通知--》目标方法--》后置通知--》返回通知
 *                  出现异常：前置通知--》目标方法--》后置通知--》异常通知
 *
 */
@EnableAspectJAutoProxy
@Configuration
public class MainConfigOfAop {

    /**
     * 业务逻辑类加入容器中
     * @return
     */
    @Bean
    public MathCalculator calculator() {
        return new MathCalculator();
    }

    /**
     * 切面类加入容器中
     */
    @Bean
    public LogAspects logAspects() {
        return new LogAspects();
    }
}
