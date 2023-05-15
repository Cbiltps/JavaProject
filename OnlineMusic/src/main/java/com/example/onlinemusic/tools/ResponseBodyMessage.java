package com.example.onlinemusic.tools;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-05-15
 * Time: 18:43
 */
@Data
public class ResponseBodyMessage <T> {
    private int status; // 状态吗

    private String message; // 返回的信息

    private T data; // 返回给前端的数据

    public ResponseBodyMessage(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
