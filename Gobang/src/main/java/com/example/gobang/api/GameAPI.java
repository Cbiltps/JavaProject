package com.example.gobang.api;

import com.example.gobang.game.GameReadyResponse;
import com.example.gobang.game.OnlineUserManager;
import com.example.gobang.game.Room;
import com.example.gobang.game.RoomManager;
import com.example.gobang.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Cbiltps
 * Date: 2023-05-08
 * Time: 9:08
 */
@Component
public class GameAPI extends TextWebSocketHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RoomManager roomManager;

    @Autowired
    private OnlineUserManager onlineUserManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        GameReadyResponse response = new GameReadyResponse();
        // 1. 先获取到当前用户的身份信息
        User user = (User) session.getAttributes().get("user");
        // 用户没有登录的情况
        if (user == null) {
            response.setOk(false);
            response.setReason("用户尚未登录!");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            return;
        }

        // 2. 判定当前用户是否已经在房间中
        Room room = roomManager.getRoomByPlayer(user.getUserId());
        if (room == null) {
            // 没找到对应的房间, 应该是玩家没有匹配到
            response.setOk(false);
            response.setReason("玩家没有匹配到!");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            return;
        }

        // 3. 判定是否多开
        if (onlineUserManager.getStatus(user.getUserId()) != null
                || onlineUserManager.getSessionStatusFromGameRoom(user.getUserId()) != null) {
            // 不仅仅是一个用户在游戏大厅算多开; 一个在游戏大厅, 一个在游戏房间也算多开
            response.setOk(false);
            response.setReason("禁止多开!");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            return;
        }

        // 4. 设置玩家上线
        onlineUserManager.enterGameRoom(user.getUserId(), session);

        // 5. 把两个游戏玩家加入到游戏房间中

     }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            // 此处简单处理, 断开连接的时候就不给客户端返回响应了
            return;
        }

        // 查看用户的在线转态, 如果在线就设为离线
        WebSocketSession exitSession = onlineUserManager.getSessionStatusFromGameRoom(user.getUserId());
        /**
         * 判定多开的时候(已经是上线状态), 需要 关闭 第二个session的时候会触发此方法并且误删第一个session,
         * 所以, 只有判断两个session一样(只有一个session)的时候才会执行 onlineUserManager.exitGameRoom(user.getUserId()); 方法!
         */
        if (session == exitSession) {
            onlineUserManager.exitGameRoom(user.getUserId());
        }
        System.out.println("当前用户 " + user.getUsername() + "游戏房间连接异常!");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            // 此处简单处理, 断开连接的时候就不给客户端返回响应了
            return;
        }

        // 查看用户的在线转态, 如果在线就设为离线
        WebSocketSession exitSession = onlineUserManager.getSessionStatusFromGameRoom(user.getUserId());
        /**
         * 判定多开的时候(已经是上线状态), 需要 关闭 第二个session的时候会触发此方法并且误删第一个session,
         * 所以, 只有判断两个session一样(只有一个session)的时候才会执行 onlineUserManager.exitGameRoom(user.getUserId()); 方法!
         */
        if (session == exitSession) {
            onlineUserManager.exitGameRoom(user.getUserId());
        }
        System.out.println("当前用户 " + user.getUsername() + "离开游戏房间!");
    }
}
