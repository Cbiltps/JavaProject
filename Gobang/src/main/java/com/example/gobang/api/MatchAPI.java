package com.example.gobang.api;

import com.example.gobang.game.MatchRequest;
import com.example.gobang.game.MatchResponse;
import com.example.gobang.game.Matcher;
import com.example.gobang.game.OnlineUserManager;
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
 * Description: 处理匹配功能中的 WebSocket 请求
 * User: cbiltps
 * Date: 2023-05-02
 * Time: 06:22
 */
@Component
public class MatchAPI extends TextWebSocketHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private OnlineUserManager onlineUserManager;

    @Autowired
    private Matcher matcher;

    /**
     * 玩家上线, 加入到 onlineUserManager 中
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        try {
            /**
             * 1. 先获取到当前用户的身份信息(谁在游戏大厅中建立的连接)~
             *
             * 之所以能够 getAttributes, 全靠了在注册 Websocket 的时候加上的代码,
             *     代码是: .addInterceptors(new HttpSessionHandshakeInterceptor());
             *
             * 这个逻辑就把 HttpSession 中的 Attribute 都给拿到 WebSocketSession 中了~
             *
             * 在登录逻辑中, 往 HttpSession 中存了 User 数据,
             *     代码是: httpSession.setAttribute("user", user);
             *
             * 此时就可以在 WebSocketSession 中把之前 HttpSession 里存的 User 对象给拿到了~
             * 但是注意, 此处拿到的 user 是有可能为 空 的!!!
             *
             * 如果之前用户压根就没有通过 HTTP 来进行登录, 直接就通过 /game_hall.html 这个 url 来访问游戏大厅页面,
             * 此时就会出现 user 为 null 的情况!
             */
            User user = (User) session.getAttributes().get("user");

            // 2. 先判定当前用户是否已经登录过(已经是在线状态), 如果是已经在线就不该继续进行后续逻辑
            WebSocketSession getWebSocketSession = onlineUserManager.getStatus(user.getUserId());
            if (onlineUserManager.getStatus(user.getUserId()) != null
                    || onlineUserManager.getSessionStatusFromGameRoom(user.getUserId()) != null) {
                // 当前用户已经登录了, 针对这个情况要告知客户端, 你这里重复登录了
                MatchResponse repeatLoginResponse = new MatchResponse();
                repeatLoginResponse.setOk(true);
                repeatLoginResponse.setReason("禁止多开!");
                repeatLoginResponse.setMessage("repeatConnection");
                // TextMessage 对应一个文本格式的 WebSocket 数据包
                getWebSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(repeatLoginResponse)));
                // 此处直接关闭有些太激进了, 还是返回一个特殊的 message , 供客户端来进行判定, 由客户端负责进行处理
//                getWebSocketSession.close();
                return;
            }

            // 3. 拿到了身份信息之后, 就可以把玩家设置成在线状态了
            onlineUserManager.enterGameHall(user.getUserId(), session);
            System.out.println("玩家 " + user.getUsername() + " 进入游戏大厅!");

        } catch (NullPointerException e) {

            e.printStackTrace();

            /**
             * 4. 出现空指针异常, 说明玩家身份信息是空的, 表示用户没有登录,
             * 所以要把 用户尚未登录的信息 返回过去!
             */
            MatchResponse exceptionResponse = new MatchResponse();
            exceptionResponse.setOk(false);
            exceptionResponse.setReason("您尚未登录, 不能进行后续的匹配功能!");
            // TextMessage 对应一个文本格式的 WebSocket 数据包
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(exceptionResponse)));
        }
    }

    /**
     * 处理开始匹配请求和停止匹配请求
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        User user = (User) session.getAttributes().get("user");
        // 获取客户端给服务器发送的数据
        String payload = message.getPayload();
        // 把 json 格式的数据载荷转化为 MatchRequest 对象
        MatchRequest request = objectMapper.readValue(payload, MatchRequest.class);

        MatchResponse response = new MatchResponse();
        if (request.getMessage().equals("startMatch")) {
            // 进入匹配队列
            matcher.addToMatchQueue(user);
            // 添加完成之后, 返回一个响应给前端
            response.setOk(true);
            response.setMessage("startMatch");
        } else if (request.getMessage().equals("stopMatch")) {
            // 退出匹配队列
            matcher.removeFromMatchQueue(user);
            // 移除完成之后, 返回一个响应给前端
            response.setOk(true);
            response.setMessage("stopMatch");
        } else {
            // 非法请求
            response.setOk(false);
            response.setMessage("非法匹配请求!");
        }
        String respJson = objectMapper.writeValueAsString(response);
        session.sendMessage(new TextMessage(respJson));
    }

    /**
     * 玩家异常下线, 退出 onlineUserManager
     * @param session
     * @param exception
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        try {
            User user = (User) session.getAttributes().get("user");
            WebSocketSession getWebSocketSession = onlineUserManager.getStatus(user.getUserId());
            if (getWebSocketSession == session) {
                onlineUserManager.exitGameHall(user.getUserId());
            }
            // 玩家在匹配的时候 WebSocket 连接异常断开了, 就把玩家从匹配队列移除
            matcher.removeFromMatchQueue(user);
        } catch (NullPointerException e) {
            e.printStackTrace();
            MatchResponse response = new MatchResponse();
            response.setOk(false);
            response.setReason("您尚未登录, 不能进行后续的匹配功能!");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }
    }

    /**
     * 玩家下线, 退出 onlineUserManager
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try {
            User user = (User) session.getAttributes().get("user");
            /**
             * 判定多开的时候(已经是上线状态), 需要 关闭 第二个session的时候会触发此方法并且误删第一个session,
             * 所以, 只有判断两个session一样(只有一个session)的时候才会执行 onlineUserManager.exitGameHall(user.getUserId()); 方法!
             */
            WebSocketSession getWebSocketSession = onlineUserManager.getStatus(user.getUserId());
            if (getWebSocketSession == session) {
                onlineUserManager.exitGameHall(user.getUserId());
            }
            // 玩家在匹配的时候 WebSocket 连接断开了, 就把玩家从匹配队列移除
            matcher.removeFromMatchQueue(user);
        } catch (NullPointerException e) {
            e.printStackTrace();
            MatchResponse response = new MatchResponse();
            response.setOk(false);
            response.setReason("出现登录异常, 不能进行后续的匹配功能!");
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }
        User user = (User) session.getAttributes().get("user");
        onlineUserManager.exitGameHall(user.getUserId());
    }
}
