package com.example.onlinemusic.mapper;

import com.example.onlinemusic.model.Music;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-05-20
 * Time: 15:04
 */
@Mapper
public interface MusicMapper {
    /**
     * 插入音乐
     * @param title
     * @param singer
     * @param time
     * @param url
     * @param userid
     * @return
     */
    int insert(String title, String singer, String time, String url, int userid);

    Music findMusic(Integer id, String title, String singer);
}
