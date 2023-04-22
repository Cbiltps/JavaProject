package com.example.gobang.api;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-04-22
 * Time: 13:20
 */
@Component
public class TestAPI extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception { // 链接建立就会触发这个方法, 可以感知到用户上线
        System.out.println("连接成功");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception { // 客服端发送文本消息, 这里可以感知消息内容
        System.out.println("收到消息:" + message.getPayload());
        // 服务器收到信息之后, 返回一个信息
        session.sendMessage(message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception { // 传输出现异常时候触发方法
        System.out.println("连接异常");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception { // 关闭链接执行的方法
        System.out.println("连接关闭");
    }
}
