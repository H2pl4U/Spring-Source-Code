package com.h2pl4u.spring.sourcecode.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.Arrays;

@Aspect
public class LogAspects {

    /**
     * 抽取公共的切入点表达式
     * 1、本类引用 @Pointcut
     * 2、其他类引用
     */
    @Pointcut("execution(public int com.h2pl4u.spring.sourcecode.aop.MathCalculator.*(..))")
    public void pointCut() {
    }

    /**
     * @Before 在目标方法之前切人，切入点表达式(指定在哪个方法切入)
     */
//    @Before("public int com.h2pl4u.spring.sourcecode.aop.MathCalculator.*(..)")
    @Before("pointCut()")
    public void logStart(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        System.out.println(joinPoint.getSignature().getName() + "运行...参数列表列表{" + Arrays.asList(args) + "}");
    }

    /**
     * @After 无论方法正常返回还是异常
     */
    @After("pointCut()")
    public void logEnd(JoinPoint joinPoint) {
        System.out.println(joinPoint.getSignature().getName() + "结束...");
    }

    /**
     * JoinPoint joinPoint一定要放在参数表第一位
     * @param joinPoint
     * @param result
     */
    @AfterReturning(value = "pointCut()", returning = "result")
    public void logReturn(JoinPoint joinPoint, Object result) {
        System.out.println(joinPoint.getSignature().getName() + "正常返回...计算结果{" + result + "}");
    }

    @AfterThrowing(value = "pointCut()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        System.out.println(joinPoint.getSignature().getName() + "运行异常...异常信息{" + exception + "}");
    }
}
