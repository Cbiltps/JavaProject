package com.example.onlinemusic.controller;

import com.example.onlinemusic.mapper.MusicMapper;
import com.example.onlinemusic.model.Music;
import com.example.onlinemusic.model.User;
import com.example.onlinemusic.tools.Constant;
import com.example.onlinemusic.tools.ResponseBodyMessage;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Value("${music.local.path}")
    private String SAVE_PATH;

    @Autowired
    MusicMapper musicMapper;

    @RequestMapping("/upload")
    public ResponseBodyMessage<Boolean> insertMusic(@RequestParam String singer,
                                                    @RequestParam("filename") MultipartFile file,
                                                    HttpServletRequest request) {
        // 1. 登录效验
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            System.out.println("没有登录!");
            return new ResponseBodyMessage<>(-1, "请登录后上传!", false);
        }

        // 2. 判读数据库中是否有当前音乐 (判断条件: 歌名+歌手 是否一致)
        String fileNameAndType = file.getOriginalFilename(); // 获取文件名及类型(xxx.mp3)
        int index = fileNameAndType.lastIndexOf(".");
        String title = fileNameAndType.substring(0, index); // 获得title
        // 获取音乐
        Music music = musicMapper.findMusic(null, title, singer);
        if (music != null) {
            return new ResponseBodyMessage<>(-1, "数据库已经有这首歌了!", false);
        }

        // 3. 上传文件至服务器(暂时在本地)
//        String fileNameAndType = file.getOriginalFilename(); // 获取文件名及类型(xxx.mp3)
        System.out.println("上传的文件: " + fileNameAndType); // 打印日志(看一下是不是需要上传的文件)
        String path = SAVE_PATH + "/" +  fileNameAndType; // 得到文件路径
        // 初始化目标文件
        File destFile = new File(path);
        // 没有此文件就创建一个
        if (!destFile.exists()) {
            destFile.mkdir();
        }
        // 上传
        try {
            file.transferTo(destFile);
//            return new ResponseBodyMessage<>(0, "上传成功!", true);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseBodyMessage<>(-1, "上传至服务器失败!", false);
        }

        // 4. 上传文件至数据库
        //  a: 准备数据
//        int index = fileNameAndType.lastIndexOf(".");
//        String title = fileNameAndType.substring(0, index);
        // 获取userid
        User user = (User) session.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();
        /**
         * 获取url(此处没有文件的后缀, 播放音乐(需要地址)的时候需要添加其后缀!)
         *
         * 此处url的作用:
         * 因为要播放音乐, 所以发送的事http请求,
         * 格式其实就是请求音乐的资源路径:   xxx + /music/get?path=title
         *
         */
        String url = "/music/get?path=" + title;
        // 获取time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sdf.format(new Date());
        //  b: 插入并返回结果
        try {
            int ret = musicMapper.insert(title, singer, time, url, userid);
            if (ret == 1) {
                return new ResponseBodyMessage<>(0, "上传至数据库成功!", true);
            } else {
                return new ResponseBodyMessage<>(-1, "上传至数据库失败!", false);
            }
        } catch (BindingException e) {
            destFile.delete();
            return new ResponseBodyMessage<>(-1, "上传至数据库失败!", false);
        }
    }

    @RequestMapping("/gettest")
    public ResponseEntity<byte[]> getTest() {
        byte[] a = {97, 98, 99, 100};
//        return ResponseEntity.internalServerError().build();
//        return ResponseEntity.notFound().build();
        return ResponseEntity.ok(a);
    }

    @RequestMapping("/get")
    public ResponseEntity<byte[]> getFileContent(String fileNameAndType) {
        File file = new File(SAVE_PATH + "/" + fileNameAndType);
        byte[] fileContent = null;
        try {
            fileContent = Files.readAllBytes(file.toPath());
            if (fileContent == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }
}
