package com.h2pl4u.spring.sourcecode.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

/**
 * 使用@Value赋值
 * 1.基本数值
 * 2.支持SpEL #{}
 * 3.支持从配置文件中获取值(环境变量中的值) ${}
 * Created on 2021/3/29 15:01
 *
 * @Author Liuwei
 */

//使用@PropertySource读取外部配置文件中的键值对保存到运行的环境变量中，加载完后使用${}取值
@PropertySource(value = {"classpath:/property.properties"})
@Data
public class PropertyEntity {
    @Value("卢本伟")
    private String name;
    @Value("#{2018-1993}")
    private Integer age;
    @Value("${property.nickName}")
    private String nickName;
}