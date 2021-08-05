package com.h2pl4u.spring.sourcecode.mvc;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * web容器创建的时候创建对象:调用方法来初始化容器以及前端控制器
 */
public class SpringMVCDemo extends AbstractAnnotationConfigDispatcherServletInitializer {
    /**
     * 获取根容器的配置类(Spring配置文件) 父容器
     * @return
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] {RootConfig.class};
    }

    /**
     * 获取web容器的配置类(SpringMVC配置文件) 子容器
     * @return
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] {AppConfig.class};
    }

    /**
     * 获取DispatcherServlet的映射信息
     *  [/]拦截所有请求(包括静态资源xx.js,xx.png,但不包括.jsp)
     *  [/*]拦截所有请求(包括静态资源xx.js,xx.png,*.jsp)
     * @return
     */
    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }
}
