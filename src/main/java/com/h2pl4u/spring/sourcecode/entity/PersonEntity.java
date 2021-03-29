package com.h2pl4u.spring.sourcecode.entity;

import lombok.Data;

/**
 * Created on 2021/3/26 13:42
 *
 * @Author Liuwei
 */
@Data
public class PersonEntity {
    private Integer id;
    private String name;
    private Integer age;
    private Boolean sex;
    private String phone;

    public void initMethod() {
        System.out.println("just init ...");
    }

    public void destroyMethod() {
        System.out.println("done ...");
    }

    public PersonEntity() {
    }

    public PersonEntity(Integer id, String name, Integer age, Boolean sex, String phone) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.phone = phone;
        System.out.println("person Constructor...");
    }
}
