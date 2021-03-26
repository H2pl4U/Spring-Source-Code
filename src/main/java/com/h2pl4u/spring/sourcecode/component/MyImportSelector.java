package com.h2pl4u.spring.sourcecode.component;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Created on 2021/3/26 14:56
 *
 * @Author Liuwei
 */
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[10];
    }
}
