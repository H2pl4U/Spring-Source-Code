package com.h2pl4u.spring.sourcecode.mvc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/**
 * SpringMVC只扫描Controller:子容器
 * useDefaultFilters = false 禁用默认过滤规则
 */
@ComponentScan(value = "com.h2pl4u.spring.sourcecode",includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class})
}, useDefaultFilters = false)
public class AppConfig {
}
