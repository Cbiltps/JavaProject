package com.example.onlinemusic.mapper;

import com.example.onlinemusic.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-05-15
 * Time: 17:49
 */
@Mapper
public interface UserMapper {

    User login(User userLogin);

    User selectUserByName(String username);

}
