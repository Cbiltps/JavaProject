package com.example.onlinemusic.controller;

import com.example.onlinemusic.tools.Constant;
import com.example.onlinemusic.tools.ResponseBodyMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-05-18
 * Time: 18:03
 */
@RestController
@RequestMapping("/music")
public class MusicController {

    private String SAVE_PATH = "/Users/cbiltps/Music/OnlineMusicFile/";

    public ResponseBodyMessage<Boolean> insertMusic(@RequestParam String singer,
                                                    @RequestParam("filename") MultipartFile file,
                                                    HttpServletRequest request) {
        // 1. 登录效验
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            System.out.println("没有登录!");
            return new ResponseBodyMessage<>(-1, "请登录后上传!", false);
        }

        // 2. 上传文件至服务器(暂时在本地)
        String fileNameAndType = file.getOriginalFilename(); // 获取文件名及类型(xxx.mp3)
        System.out.println("上传的文件: " + fileNameAndType); // 打印日志(看一下是不是需要上传的文件)
        String path = SAVE_PATH + fileNameAndType; // 得到文件路径

        File destFile = new File(path);

        // 没有此文件就创建一个
        if (!destFile.exists()) {
            destFile.mkdir();
        }

        try {
            file.transferTo(destFile); // 上传
            return new ResponseBodyMessage<>(0, "上传成功!", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 上传失败
        return new ResponseBodyMessage<>(-1, "上传失败", false);
    }
}
