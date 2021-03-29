package com.h2pl4u.spring.sourcecode.component;

import com.h2pl4u.spring.sourcecode.entity.ColorEntity;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created on 2021/3/29 9:46
 *
 * @Author Liuwei
 */
public class ColorFactoryBean implements FactoryBean<ColorEntity> {
    @Override
    public ColorEntity getObject() throws Exception {
        System.out.println("ColorFactoryBean...");
        return new ColorEntity();
    }

    @Override
    public Class<?> getObjectType() {
        return ColorEntity.class;
    }

    /**
     * 单例控制，true单例，false多实例
     * @return
     */
    @Override
    public boolean isSingleton() {
        return true;
    }
}
