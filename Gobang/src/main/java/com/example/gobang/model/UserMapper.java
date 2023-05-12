package com.example.gobang.model;

import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-04-22
 * Time: 21:36
 */
@Mapper
public interface UserMapper {
    // 往数据库里插入一个用户---用于注册功能.
    void insert(User user);

    // 根据用户名查询用户的详细信息---用于登录功能
    User selectByName(String username);

    // 总比赛场数+1, 获胜场数+1, 天梯分数+30
    void userWin(int userId);

    // 总比赛场数+1, 获胜场数不变, 天梯分数-30
    void userLose(int userId);
}
