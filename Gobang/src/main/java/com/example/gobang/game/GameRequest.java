package com.example.gobang.game;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description: 表示落子请求
 * User: Cbiltps
 * Date: 2023-05-08
 * Time: 9:28
 */
@Data
public class GameRequest {
    private String message;
    private int userId;
    private int row;
    private int col;
}
