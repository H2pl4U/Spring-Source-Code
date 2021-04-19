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
     * @return
     */
    @Bean
    public LogAspects logAspects() {
        return new LogAspects();
    }
}
