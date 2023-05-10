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

import java.io.IOException;

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
            response.setOk(true);
            response.setReason("禁止多开!");
            response.setMessage("repeatConnection");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            return;
        }

        // 4. 设置玩家上线
        onlineUserManager.enterGameRoom(user.getUserId(), session);

        /**
         * 5. 把两个游戏玩家加入到游戏房间中
         *
         * 当前逻辑是在 game_room 页面加载的时候进行的,
         * 前面在 创建房间/匹配 的过程中构造的时候没有为 User 赋值.
         * 
         * 匹配到对手之后经过页面跳转, 来到 game_room.html 才算正式进入游戏房间, 才算玩家准备就绪!\
         * 换句话说, 执行当前逻辑就算页面跳转成功了!
         */
        synchronized (room) {
            if (room.getPlayer1() == null) {
                // 玩家1还未加入房间, 就把当前连上 WebSocket 的玩家作为 玩家1 加入房间
                room.setPlayer1(user);
                // 先连入房间的玩家就是先手方(也可以设置随机数, 比较灵活)
                room.setWhiteUser(user.getUserId());
                System.out.println("玩家1 " + user.getUsername() + " 准备就绪!");
                return;
            }

            if (room.getPlayer2() == null) {
                // 玩家1加入房间, 玩家2还未加入房间, 就把当前连上 WebSocket 的玩家作为 玩家2 加入房间
                room.setPlayer2(user);
                System.out.println("玩家2 " + user.getUsername() + " 准备就绪!");
                // 两个玩家都加入成功之后, 就给两个玩家返回 WebSocket 响应, 通知双方都已经准备完毕
                noticeGameReady(room, room.getPlayer1(), room.getPlayer2()); // 给玩家1通知
                noticeGameReady(room, room.getPlayer2(), room.getPlayer1()); // 给玩家2通知
                return;
            }
        }

        // 6. 如果有新的玩家尝试连接同一个房间, 给出提示报错(一般不会出现, 为了程序的健壮性还是做一个判定和提示)
        response.setOk(false);
        response.setReason("当前房间满了, 无法加入房间");
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private void noticeGameReady(Room room, User thisUser, User thatUser) throws IOException {
        GameReadyResponse response = new GameReadyResponse();
        response.setMessage("gameReady");
        response.setOk(true);
        response.setReason("");
        response.setRoomId(room.getRoomId());
        response.setThisUserId(thisUser.getUserId());
        response.setThatUserId(thatUser.getUserId());
        response.setWhiteUser(room.getWhiteUser());

        // 把当前的响应数据转回给玩家
        WebSocketSession session = onlineUserManager.getSessionStatusFromGameRoom(thisUser.getUserId());
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 1. 先从 session 里拿到当前用户的身份信息
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            System.out.println("[handleTextMessage]: 当前玩家尚未登录!");
            return;
        }
        // 2. 根据玩家 id 获取到房间对象
        Room room = roomManager.getRoomByPlayer(user.getUserId());
        // 3. 通过 room 对象来处理这次具体的请求
        room.putChess(message.getPayload());
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
