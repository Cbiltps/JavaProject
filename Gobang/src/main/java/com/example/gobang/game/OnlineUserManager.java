package com.example.gobang.game;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description: 维护用户的在线状态
 * User: cbiltps
 * Date: 2023-05-03
 * Time: 15:07
 */
@Component
public class OnlineUserManager {

    /**
     * 之所以要维护用户的在线状态, 目的是为了能够在代码中比较方使的获取到某个用户当前的 WebSocket 会话,
     * 从而可以通过这个会话来给这个客户端发送消总!
     * 同时也可以感知到他的在线/离线状态~
     *
     * 就可以使用一个 哈希表 来保存当前用户的在线状态~~
     * key-就是用户 id~
     * value-就是用户当前使用的 WebSocket 会话~
     */

    // 回去学习一下ConcurrentHashMap, 老师讲的比较模糊!

    // 表示当前用户在游戏大厅的在线转态
    private ConcurrentHashMap<Integer, WebSocketSession> onlineStatus = new ConcurrentHashMap<>();

    // 表示当前用户在游戏房间的在线转态
    private ConcurrentHashMap<Integer, WebSocketSession> onlineStatusForGameRoom = new ConcurrentHashMap<>();

    /**
     * 进入游戏大厅的状态: 在线
     * @param userId
     * @param webSocketSession
     */
    public void enterGameHall(int userId, WebSocketSession webSocketSession) {
        onlineStatus.put(userId, webSocketSession);
    }

    /**
     * 离开游戏大厅的状态: 离线
     * @param userId
     */
    public void exitGameHall(int userId) {
        onlineStatus.remove(userId);
    }

    /**
     * 获取 WebSocketSession
     * @param userId
     * @return
     */
    public WebSocketSession getStatus(int userId) {
        return onlineStatus.get(userId);
    }

    public void enterGameRoom(int userId, WebSocketSession webSocketSession) {
        onlineStatusForGameRoom.put(userId, webSocketSession);
    }

    public void exitGameRoom(int userId) {
        onlineStatusForGameRoom.remove(userId);
    }

    public WebSocketSession getSessionStatusFromGameRoom(int userId) {
        return  onlineStatusForGameRoom.get(userId);
    }
}
