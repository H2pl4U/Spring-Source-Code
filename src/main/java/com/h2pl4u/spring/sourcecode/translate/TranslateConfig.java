package com.h2pl4u.spring.sourcecode.translate;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Created on 2021/5/17 11:52
 *
 * @Author Liuwei
 */

/**
 * 声明式事务：
 *  环境搭建：
 *      1.导入相关依赖
 *          数据源、数据库驱动，Spring-jdbc模块
 *      2.配置数据源 JdbcTemplate(Spring提供的简化数据库操作的工具) 操作数据
 *      3.@Transactional 标识spring事务方法
 *      4.@EnableTransactionManagement 开启基于注解的事务管理功能
 *      5.配置事务管理器来控制事务
 *          @Bean
 *          public PlatformTransactionManager transactionManager()
 *
 *  spring事务原理：
 *      1.@EnableTransactionManagement 利用TransactionManagementConfigurationSelector 给容器中会导入组件
 *          导入两个组件:AutoProxyRegistrar 和 ProxyTransactionManagementConfiguration
 *      2.AutoProxyRegistrar：给容器中注册一个 InfrastructureAdvisorAutoProxyCreator 组件
 *          InfrastructureAdvisorAutoProxyCreator 利用后置处理器机制在对象创建后包装对象并返回一个代理对象(增强器)，代理对象执行方法利用拦截器链进行调用
 *      3.ProxyTransactionManagementConfiguration
 *          给容器中注册事务增强器
 *              ①事务增强器要用事务注解信息，AnnotationTransactionAttributeSource 解析事务注解
 *              ②事务拦截器，TransactionInterceptor：保存了事务属性信息，事务管理器，底层是MethodInterceptor，在目标方法执行时执行拦截器链
 *                  先获取事务相关属性，再获取PlatformTransactionManager，如果事先没有添加指定任何transactionManager，
 *                  最终会从容器中按类型获取一个PlatformTransactionManager，执行目标方法如果异常，获取到事务管理器，利用事务管理器回滚操作，
 *                  如果正常，利用事务管理器提交事务
 */
@EnableTransactionManagement
@ComponentScan("com.h2pl4u.spring.sourcecode.translate")
@Configuration
public class TranslateConfig {
    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test02?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        DataSource dataSource = dataSource();
        return new JdbcTemplate(dataSource);
    }

    /**
     * 注册事务管理器在容器中
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

}
