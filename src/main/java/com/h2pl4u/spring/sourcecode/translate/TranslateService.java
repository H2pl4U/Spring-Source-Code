package com.h2pl4u.spring.sourcecode.translate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created on 2021/5/17 13:19
 *
 * @Author Liuwei
 */

@Service
public class TranslateService {
    @Autowired
    private TranslateDao translateDao;

    @Transactional
    public void insert() {
        translateDao.insert();
        System.out.println("执行完毕");
//        int i = 1 / 0;
    }

}
