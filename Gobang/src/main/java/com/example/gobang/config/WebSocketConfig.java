package com.example.gobang.config;

import com.example.gobang.api.GameAPI;
import com.example.gobang.api.TestAPI;
import com.example.gobang.api.MatchAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * Created with IntelliJ IDEA.
 * Description: 注册 WebSocketHandler 的核心配置类
 * User: cbiltps
 * Date: 2023-04-22
 * Time: 13:31
 */
@Configuration // Spring中常用的注解
@EnableWebSocket // 表示配置WebSocket类, 开启WebSocket的关键所在
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private TestAPI testAPI;

    @Autowired
    private MatchAPI matchAPI;

    @Autowired
    private GameAPI gameAPI;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) { // 注册一些Handler到框架里面去
        registry.addHandler(testAPI, "/test") // 客户端连接到这个路径之后, 就会调用到testAPI, 然后再去调用TestAPI类中的方法
                /**
                 * 注意:注册API实例, 经查阅官方文档 Spring WebSocket4.1.5 版本前默认支持跨域访问,
                 * 之后的版本默认不支持跨域(403), 需要设置setAllowedOrigins()
                 */
                .setAllowedOrigins("*");

        registry.addHandler(matchAPI, "/findMatch")
                /**
                 * 用户登录就会给 HttpSession 中保存用户的信息~~
                 * 在注册 WebSocketAPl 的时候, 就常要把前面准备好的 HttpSession 给搞过来(搞到WebSocket的Session中)
                 */
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*");

        registry.addHandler(gameAPI, "/game")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*");
    }
}
