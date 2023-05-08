package com.example.gobang.game;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description: 客户端连接到游戏房间后, 服务器返回的响应
 * User: Cbiltps
 * Date: 2023-05-08
 * Time: 9:20
 */
@Data
public class GameReadyResponse {
    private String message;
    private boolean ok;
    private String reason;
    private String roomId;
    private int thisUserId;
    private int thatUserId;
    private int whiteUser;
}
