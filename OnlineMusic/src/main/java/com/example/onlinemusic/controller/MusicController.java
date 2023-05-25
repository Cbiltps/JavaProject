package com.example.onlinemusic.controller;

import com.example.onlinemusic.mapper.LoveMusicMapper;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    private MusicMapper musicMapper;

    @Autowired
    private LoveMusicMapper loveMusicMapper;

    @RequestMapping("/upload")
    public ResponseBodyMessage<Boolean> insertMusic(@RequestParam String singer,
                                                    @RequestParam("filename") MultipartFile file,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws IOException {
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
                // 跳转到音乐列表页面
                response.sendRedirect("/list.html");
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

    /**
     * 播放音乐
     * @param filenameandtype
     * @return
     */
    @RequestMapping("/get")
    public ResponseEntity<byte[]> getFileContent(String filenameandtype) {
        File file = new File(SAVE_PATH + "/" + filenameandtype);
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

    @RequestMapping("/delete")
    public ResponseBodyMessage<Boolean> deleteMusicById(@RequestParam String musicid) {
        // 1. 检查音乐在数据库中是否存在
        int musicIdOfTypeInt = Integer.parseInt(musicid);
        Music music = musicMapper.findMusic(musicIdOfTypeInt, null, null);
        // 2. 没找到既没有删除的音乐
        if (music == null) {
            return new ResponseBodyMessage<>(-1, "没有你要删除的音乐!", false);
        }
        // 3. 找到既删除音乐
        int result = musicMapper.deleteMusicById(musicIdOfTypeInt);
        System.out.println(result);
        if (1 == result) {
            // 4. 同时删除服务器本地数据(前面上传的时候, 先存在本地然后存储到服务器)
            int index = music.getUrl().lastIndexOf("=");
            String fileName = music.getUrl().substring(index + 1);
            File file = new File(SAVE_PATH + "/" + fileName + ".mp3");
            System.out.println("当前文件路径: " + file.toPath());
            if (file.delete()) {
                // 5. 同时删除lovemusic表中的歌曲
                loveMusicMapper.deleteLoveMusicByMusicId(musicIdOfTypeInt);
                return new ResponseBodyMessage<>(0, "删除成功!", true);
            } else {
                return new ResponseBodyMessage<>(-1, "删除失败!", false);
            }
        } else {
            return new ResponseBodyMessage<>(-1, "数据库中的音乐没有删除成功!", false);
        }
    }

    /**
     * 批量删除音乐(删除选中的音乐)
     * @param ids
     * @return
     */
    @RequestMapping("/deleteselectedmusic")
    public ResponseBodyMessage<Boolean> deleteSelectedMusic(@RequestParam List<Integer> ids) {
        System.out.println("所有需要删除的id: " + ids);
        int count = 0;
        for (int i = 0; i < ids.size(); i++) {
            // 准备资源-musicId
            int musicId = ids.get(i);
            // 1. 检查音乐在数据库中是否存在
            Music music = musicMapper.findMusic(musicId, null, null);
            // 2. 没找到既没有删除的音乐
            if (music == null) {
                return new ResponseBodyMessage<>(-1, "没有你要删除的音乐!", false);
            }
            // 3. 找到既删除音乐
            int result = musicMapper.deleteMusicById(musicId);
            if (1 == result) {
                // 4. 同时删除服务器本地数据(前面上传的时候, 先存在本地然后存储到服务器)
                int index = music.getUrl().lastIndexOf("=");
                String fileName = music.getUrl().substring(index + 1);
                File file = new File(SAVE_PATH + "/" + fileName + ".mp3");
                System.out.println("当前文件路径: " + file.toPath());
                if (file.delete()) {
                    // 5. 同时删除lovemusic表中的歌曲
                    loveMusicMapper.deleteLoveMusicByMusicId(musicId);
                    count += result;
//                    return new ResponseBodyMessage<>(0, "删除成功!", true);
                } else {
                    return new ResponseBodyMessage<>(-1, "删除失败!", false);
                }
            } else {
                return new ResponseBodyMessage<>(-1, "数据库中的音乐没有删除成功!", false);
            }
        }
        if(count == ids.size()) {
            System.out.println("批量删除成功！");
            return new ResponseBodyMessage<>(0,"音乐批量删除成功!",true);
        }else {
            System.out.println("批量删除失败！");
            return new ResponseBodyMessage<>(-1,"音乐批量删除失败!", false);
        }
    }

    @RequestMapping("/findmusic")
    public ResponseBodyMessage<List<Music>> findMusic(@RequestParam(required = false) String musicname) {
        List<Music> musicList = null;
        if(musicname != null) {
            // 这是一个模糊查询
            musicList = musicMapper.findMusicByName(musicname);
        }else {
            // 查询所有音乐
            musicList = musicMapper.findAllMusic();
        }
        return new ResponseBodyMessage<>(0, "查询到了相关音乐", musicList);
    }
}
