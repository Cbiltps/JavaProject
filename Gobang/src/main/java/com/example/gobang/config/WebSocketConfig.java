package com.example.gobang.config;

import com.example.gobang.api.TestAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-04-22
 * Time: 13:31
 */
@Configurable // Spring中常用的注解
@EnableWebSocket // 表示配置WebSocket类, 开启WebSocket的关键所在
public class WebSocketConfig implements WebSocketConfigurer { // 注册一些
    @Autowired
    private TestAPI testAPI;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) { // 注册一些Handler到框架里面去
        // 注册API实例
        registry.addHandler(testAPI, "/test"); // 客户端连接到这个路径之后, 就会调用到testAPI, 然后再去调用TestAPI类中的方法
    }
}
