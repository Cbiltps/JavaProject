package com.example.gobang.game;

import com.example.gobang.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * Description: 匹配器(这个类负责整个匹配功能)
 * User: cbiltps
 * Date: 2023-05-04
 * Time: 13:18
 */
@Component
public class Matcher {
    // 创建三个匹配队列
    private Queue<User> normalQueue = new LinkedList<>();
    private Queue<User> highQueue = new LinkedList<>();
    private Queue<User> proQueue = new LinkedList<>();

    @Autowired
    private OnlineUserManager onlineUserManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    public Matcher() {
        // 创建三个线程针对三个匹配队列进行操作
        Thread normalThread = new Thread() {
            @Override
            public void run() {
                // 扫描 normalQueue 队列
                while (true) {
                    handlerMatch(normalQueue);
                }
            }
        };
        normalThread.start();

        Thread highThread = new Thread() {
            @Override
            public void run() {
                // 扫描 highQueue 队列
                while (true) {
                    handlerMatch(highQueue);
                }
            }
        };
        highThread.start();

        Thread proThread = new Thread() {
            @Override
            public void run() {
                // 扫描 proQueue 队列
                while (true) {
                    handlerMatch(proQueue);
                }
            }
        };
        proThread.start();
    }

    private void handlerMatch(Queue<User> matchQueue) {
        synchronized (matchQueue) {
            try {
                /**
                 * 1. 检测队列中的元素是否有两个以上
                 *
                 * 使用 while 循环的原因:
                 * 队列的初始情况可能是 空 !
                 * 如果往队列中添加一个元素, 这个时候仍然是不能进行后续匹配操作的,
                 * 因此在这里使用 while 循环检查是更合理的~~
                 */
                while (matchQueue.size() < 2) {
                    matchQueue.wait(); // 两个玩家以下就进行等待, 有新玩家加入的时候进行通知
                }

                // 2. 从队里中取出两个玩家
                User player1 = matchQueue.poll();
                User player2 = matchQueue.poll();
                System.out.println("匹配出两个玩家: " + player1.getUsername() + ", " + player2.getUsername());

                // 3. 获取玩家的 WebSocketSession, 目的是告诉玩家匹配成功
                WebSocketSession session1 = onlineUserManager.getStatus(player1.getUserId());
                WebSocketSession session2 = onlineUserManager.getStatus(player1.getUserId());
                /**
                 * 理论上来说, 匹配队列中的玩家一定是在线的状态
                 * 因为前面的逻辑里进行了处理, 当玩家断开连接的时候就把玩家从匹配队列中移除了
                 * 但是此处仍然进行一次判定~~
                 */
                if (session1 == null) {
                    // 如果 玩家1 现在不在线了, 就把 玩家2 重新放回到匹配队列中
                    matchQueue.offer(player2);
                    return;
                }
                if (session2 == null) {
                    // 如果 玩家2 现在不在线了, 就把 玩家1 重新放回到匹配队列中
                    matchQueue.offer(player1);
                    return;
                }
                /**
                 *  当前能否排到两个玩家是同一个用户的情况嘛? 一个玩家入队列了两次?
                 *  理论上也不会存在~~
                 *      1: 如果玩家下线, 就会对玩家移出匹配队列
                 *      2: 又禁止了玩家多开
                 *  但是仍然在这里多进行一次判定, 以免前面的逻辑出现 bug 时带来严重的后果
                 */
                if (session1 == session2) {
                    // 把其中的一个玩家放回匹配队列
                    matchQueue.offer(player1);
                    return;
                }

                // TODO 4. 把这两个玩家放到一个游戏房间中.

                // 5. 给玩家反馈信息: 你匹配到对手了~
                // 通过 WebSocket 返回一个 message 为 'matchSuccess' 这样的响应
                // 此处是要给两个玩家都返回 "匹配成功" 这样的信息, 因此就需要返回两次
                MatchResponse response1 = new MatchResponse();
                response1.setOk(true);
                response1.setMessage("matchSuccess");
                String respJson1 = objectMapper.writeValueAsString(response1);
                session1.sendMessage(new TextMessage(respJson1));

                MatchResponse response2 = new MatchResponse();
                response2.setOk(true);
                response2.setMessage("matchSuccess");
                String respJson2 = objectMapper.writeValueAsString(response2);
                session2.sendMessage(new TextMessage(respJson2));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 点击匹配的时候, 把玩家放到匹配队列中
     * @param user
     */
    public void addToMatchQueue(User user) {
        if (user.getScore() < 2000) {
            synchronized (normalQueue) {
                normalQueue.offer(user);
                normalQueue.notify(); // 有新玩家加入的时候通知, 然后进行后续的唤醒并尝试获取锁
            }
            System.out.println("玩家 " + user.getUsername() + "进入 normalQueue 中!");
        } else if (user.getScore() >= 2000 && user.getScore() < 3000) {
            synchronized (highQueue) {
                highQueue.offer(user);
                highQueue.notify(); // 有新玩家加入的时候通知, 然后进行后续的唤醒并尝试获取锁
            }
            System.out.println("玩家 " + user.getUsername() + "进入 highQueue 中!");
        } else {
            synchronized (proQueue) {
                proQueue.offer(user);
                proQueue.notify(); // 有新玩家加入的时候通知, 然后进行后续的唤醒并尝试获取锁
            }
            System.out.println("玩家 " + user.getUsername() + "进入 proQueue 中!");
        }
    }

    /**
     * 点击停止匹配的时候, 把玩家从匹配队列中移除
     * @param user
     */
    public void removeFromMatchQueue(User user) {
        if (user.getScore() < 2000) {
            synchronized (normalQueue) {
                normalQueue.remove(user);
            }
            System.out.println("玩家 " + user.getUsername() + "从 normalQueue 中退出!");
        } else if (user.getScore() >= 2000 && user.getScore() < 3000) {
            synchronized (highQueue) {
                highQueue.remove(user);
            }
            System.out.println("玩家 " + user.getUsername() + "从 highQueue 中退出!");
        } else {
            synchronized (proQueue) {
                proQueue.remove(user);
            }
            System.out.println("玩家 " + user.getUsername() + "从 proQueue 中退出!");
        }
    }
}
