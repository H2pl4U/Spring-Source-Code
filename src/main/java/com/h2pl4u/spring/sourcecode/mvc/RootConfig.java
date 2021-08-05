package com.h2pl4u.spring.sourcecode.mvc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/**
 * Spring容器不扫描Controller:父容器
 */
@ComponentScan(value = "com.h2pl4u.spring.sourcecode",excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class})
})
public class RootConfig {
}
