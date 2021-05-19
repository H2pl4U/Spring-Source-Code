package com.h2pl4u.spring.sourcecode.extension;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Created on 2021/5/19 17:10
 *
 * @Author Liuwei
 */
@Service
public class ListenerService {

    @EventListener(classes = {ApplicationEvent.class})
    public void listen(ApplicationEvent event) {
        System.out.println("ListenerService 监听到事件：" + event);
    }
}
