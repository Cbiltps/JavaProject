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
import java.util.List;

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
    private LoveMusicMapper loveMusicMapper;

    /**
     * 收藏音乐
     * @param musicid
     * @param request
     * @return
     */
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

    @RequestMapping("/findlovemusic")
    public ResponseBodyMessage<List<Music>> findMusic(@RequestParam(required = false) String musicname ,HttpServletRequest request) {
        // 登录效验
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            System.out.println("没有登录!");
            return new ResponseBodyMessage<>(-1, "请登录后查询!", null);
        }

        // 获取参数
        User user = (User) session.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();

        // 查询相关数据
        List<Music> musicList = null;
        if (musicname == null) {
            musicList = loveMusicMapper.findLoveMusicByUserId(userId);
        } else {
            musicList = loveMusicMapper.findLoveMusicByUserIdAndMusicName(userId, musicname);
        }

        // 返回结果
        if (musicList.isEmpty()) {
            return new ResponseBodyMessage<>(-1, "未获取到相关音乐!", null);
        } else {
            return new ResponseBodyMessage<>(0, "获取到相关音乐!", musicList);
        }
    }

    @RequestMapping("/deletelovemusic")
    public ResponseBodyMessage<Boolean> deleteLoveMusic(String musicid, HttpServletRequest request) {
        // 登录效验
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            System.out.println("没有登录!");
            return new ResponseBodyMessage<>(-1, "请登录后查找!", false);
        }

        // 准备参数
        User user = (User) session.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();
        int loveMusicIdOfTypeInt = Integer.parseInt(musicid);

        // 删除
        int result = loveMusicMapper.deleteLoveMusic(userId, loveMusicIdOfTypeInt);

        // 返回结果
        if (1 == result) {
            return new ResponseBodyMessage<>(0, "取消收藏成功!", true);
        } else {
            return new ResponseBodyMessage<>(-1, "取消收藏失败!", false);
        }
    }
}
