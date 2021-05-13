package com.h2pl4u.spring.sourcecode.component;

import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * Created on 2021/3/26 14:19
 *
 * @Author Liuwei
 */
public class MyFilterType implements TypeFilter {
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        //获取当前类注解信息
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        //获取正在扫描类的类信息
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        //获取当前类资源(路径)
        Resource resource = metadataReader.getResource();
        String className = classMetadata.getClassName();
//        System.out.println("--------------->" + className);
        if (className.contains("er")) {
            return true;
        }
        return false;
    }
}
