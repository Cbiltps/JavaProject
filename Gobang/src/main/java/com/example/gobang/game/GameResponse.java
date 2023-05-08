package com.example.gobang.game;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description: 表示一个落子响应
 * User: Cbiltps
 * Date: 2023-05-08
 * Time: 9:31
 */
@Data
public class GameResponse {
    private String message;
    private int userId;
    private int row;
    private int col;
    private int winner;
}
