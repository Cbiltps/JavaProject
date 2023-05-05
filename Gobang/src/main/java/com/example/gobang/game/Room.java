package com.example.gobang.game;

import com.example.gobang.model.User;
import lombok.Data;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description: 表示一个游戏房间
 * User: cbiltps
 * Date: 2023-05-05
 * Time: 10:24
 */
@Data
public class Room {
    // 使用字符串类型来表示, 方便生成唯一值
    private String roomId;

    private User player1;
    private User player2;

    public Room() {
        // 构造 Room 的时候生成一个唯一的字符串表示房间 id, 使用 UUID 来作为房间 id~~
        roomId = UUID.randomUUID().toString();
    }
}
