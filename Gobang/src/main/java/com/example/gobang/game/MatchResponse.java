package com.example.gobang.game;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description: WebSocket 匹配响应
 * User: cbiltps
 * Date: 2023-05-03
 * Time: 17:19
 */
@Data
public class MatchResponse {
    private boolean ok;
    private String reason;
    private String message;
}
