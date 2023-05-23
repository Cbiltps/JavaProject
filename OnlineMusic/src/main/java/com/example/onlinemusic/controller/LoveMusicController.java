package com.example.onlinemusic.controller;

import com.example.onlinemusic.mapper.LoveMusicMapper;
import com.example.onlinemusic.model.Music;
import com.example.onlinemusic.model.User;
import com.example.onlinemusic.tools.Constant;
import com.example.onlinemusic.tools.ResponseBodyMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-05-23
 * Time: 13:30
 */
@RestController
@RequestMapping("/lovemusic")
public class LoveMusicController {

    @Resource
    LoveMusicMapper loveMusicMapper;

    @RequestMapping("/insertlovemusic")
    public ResponseBodyMessage<Boolean> insertlovemusic(@RequestParam String musicid, HttpServletRequest request) {

        // 登录效验
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            System.out.println("没有登录!");
            return new ResponseBodyMessage<>(-1, "请登录后插入!", false);
        }

        // 准备参数
        User user = (User) session.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();
        int musicIdOfTypeInt = Integer.parseInt(musicid);
        System.out.println("当前收藏音乐信息: userId: " + userId + "   musicId: " + musicIdOfTypeInt);

        // 收藏验证
        Music music = loveMusicMapper.findLoveMusicByUserIdAndMusicId(userId, musicIdOfTypeInt);
        if (music != null) {
            // 收藏过此音乐
            return new ResponseBodyMessage<>(-1, "您之前收藏过此音乐!", false);
        }

        // 插入收藏数据库
        boolean result = loveMusicMapper.insertLoveMusic(userId, musicIdOfTypeInt);
        if (result) {
            return new ResponseBodyMessage<>(0, "收藏成功!", true);
        } else {
            return new ResponseBodyMessage<>(-1, "收藏失败!", false);
        }
    }

    // TODO 添加取消收藏音乐功能



}
