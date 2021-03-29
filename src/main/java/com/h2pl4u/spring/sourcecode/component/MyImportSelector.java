package com.h2pl4u.spring.sourcecode.component;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 自定义逻辑返回需要导入的组件
 * <p>
 * Created on 2021/3/26 14:56
 *
 * @Author Liuwei
 */
public class MyImportSelector implements ImportSelector {
    /**
     * 返回值为导入到容器中的组件全类名
     *
     * @param annotationMetadata 当前标注@Import注解类的所有注解信息
     * @return
     */
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{"com.h2pl4u.spring.sourcecode.entity.BlueColor", "com.h2pl4u.spring.sourcecode.entity.RedColor"};
    }
}
