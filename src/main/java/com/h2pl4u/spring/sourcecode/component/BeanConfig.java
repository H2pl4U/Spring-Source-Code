package com.h2pl4u.spring.sourcecode.component;

import com.h2pl4u.spring.sourcecode.dao.BookDao;
import com.h2pl4u.spring.sourcecode.entity.ColorEntity;
import com.h2pl4u.spring.sourcecode.entity.PersonEntity;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;

/**
 * Created on 2021/3/26 13:41
 *
 * @Author Liuwei
 */
@ComponentScan(value = "com.h2pl4u.spring.sourcecode", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class})
}, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {BookDao.class}),
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = {MyFilterType.class})
}, useDefaultFilters = false)
@Configuration
@Lazy
//快速导入组件，id默认是组件的全类名
@Import({ColorEntity.class,PersonEntity.class, MyImportSelector.class})
public class BeanConfig {
    @Bean(value = "jbh", initMethod = "initMethod")
    public PersonEntity person01() {
        return new PersonEntity(1, "jbh", 22, false, "12345678912");
    }

    @Bean(name = "jjf", destroyMethod = "destroyMethod")
    public PersonEntity person02() {
        return new PersonEntity(2, "jjf", 22, false, "12345678911");
    }

    /**
     * 给容器中注册组件：
     * 1)包扫描+组件标注注解(@Controller/@Service/@Repository/@Component) [自定义]
     * 2)@Bean [导入第三方包里面的组件]
     * 3)@Import:
     *      1.@Import [快速给容器中导入组件,id默认是组件的全类名]
     *      2.@ImportSelector [快速给容器中导入组件,返回值是组件的全类名]
     */
}
