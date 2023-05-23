package com.example.onlinemusic.mapper;

import com.example.onlinemusic.model.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-05-23
 * Time: 13:23
 */
@Mapper
public interface LoveMusicMapper {
    /**
     * 查询喜欢的音乐
     * @param userid
     * @param musicid
     * @return
     */
    Music findLoveMusicByUserIdAndMusicId(int userid, int musicid);

    /**
     * 收藏音乐
     * @param userid
     * @param musicid
     * @return
     */
    boolean insertLoveMusic(int userid, int musicid);

    /**
     * 查询当前用户的收藏歌单
     * @param userid
     * @return
     */
    List<Music> findLoveMusicByUserId(int userid);

    /**
     * 通过查询当前用户的收藏歌单
     * @param musicname
     * @param userid
     * @return
     */
    List<Music> findLoveMusicByUserIdAndMusicName(int userid, String musicname);
}
