package com.h2pl4u.spring.sourcecode.translate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created on 2021/5/17 13:20
 *
 * @Author Liuwei
 */
@Repository
public class TranslateDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insert() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        String sql = "insert into user (name,age,created_at,updated_at) values(?,?,?,?)";
        jdbcTemplate.update(sql, "jjh", 22, now, now);
    }
}
