package com.h2pl4u.spring.sourcecode.propertyValue;

import com.h2pl4u.spring.sourcecode.entity.PropertyEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2021/3/29 14:58
 *
 * @Author Liuwei
 */
@Configuration
public class MainConfig4PropertyValues {
    @Bean("property")
    public PropertyEntity property01() {
        return new PropertyEntity();
    }
}
