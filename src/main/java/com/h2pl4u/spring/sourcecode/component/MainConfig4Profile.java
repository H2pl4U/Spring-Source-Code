package com.h2pl4u.spring.sourcecode.component;

import com.alibaba.druid.pool.DruidDataSource;
import com.h2pl4u.spring.sourcecode.entity.BlueColor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringValueResolver;

import javax.sql.DataSource;

/**
 * Profile:
 *      Spring提供的可根据当前环境动态的激活和切换一系列组件的功能
 * 开发环境/测试环境/生产环境
 * 1).加了环境标识的bean，只有被此环境被激活时才能注册到容器中，默认default
 */
@PropertySource("classpath:/dbconfig.properties")
@Configuration
public class MainConfig4Profile implements EmbeddedValueResolverAware {

    @Value("${db.username}")
    private String username;
    private String driverClass;

    private StringValueResolver valueResolver;


    @Profile("test")
    @Bean
    public BlueColor blueColor() {
        return new BlueColor();
    }

    @Profile("test")
    @Bean("testDataSource")
    public DataSource dataSourceTest(@Value("${db.password}") String pwd) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername(username);
        dataSource.setPassword(pwd);
        dataSource.setUrl("jdbc:mysql://localhost:3306/one_db");
        dataSource.setDriverClassName(driverClass);
        return dataSource;
    }

    @Profile("dev")
    @Bean("devDataSource")
    public DataSource dataSourceDev(@Value("${db.password}") String pwd) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername(username);
        dataSource.setPassword(pwd);
        dataSource.setUrl("jdbc:mysql://localhost:3306/one_db");
        dataSource.setDriverClassName(driverClass);
        return dataSource;
    }

    @Profile("pro")
    @Bean("proDataSource")
    public DataSource dataSourcePro(@Value("${db.password}") String pwd) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername(username);
        dataSource.setPassword(pwd);
        dataSource.setUrl("jdbc:mysql://localhost:3306/two_db");
        dataSource.setDriverClassName(driverClass);
        return dataSource;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.valueResolver = stringValueResolver;
        this.driverClass = valueResolver.resolveStringValue("${db.driverClass}");
    }
}
