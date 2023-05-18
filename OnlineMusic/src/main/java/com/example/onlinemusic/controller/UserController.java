package com.example.onlinemusic.controller;

import com.example.onlinemusic.mapper.UserMapper;
import com.example.onlinemusic.model.User;
import com.example.onlinemusic.tools.Constant;
import com.example.onlinemusic.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-05-15
 * Time: 18:07
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @RequestMapping("/logintest")
    public ResponseBodyMessage<User> loginTest(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        User userLogin = new User();
        userLogin.setUsername(username);
        userLogin.setPassword(password);

        User user = userMapper.login(userLogin);

        if (user == null) {
            System.out.println("登录失败!");
            return new ResponseBodyMessage<>(-1, "登录失败!", userLogin);
        } else {
            request.getSession().setAttribute(Constant.USERINFO_SESSION_KEY, user);
            return new ResponseBodyMessage<>(0, "登录成功!", userLogin);
        }
    }

    @RequestMapping("/login")
    public ResponseBodyMessage<User> login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        User user = userMapper.selectUserByName(username);

        if (user == null) {
            System.out.println("登录失败!");
            return new ResponseBodyMessage<>(-1, "用户名或者密码错误!", user);
        } else {
            boolean flag = encoder.matches(password, user.getPassword());
            if (!flag) {
                return new ResponseBodyMessage<>(-1, "用户名或者密码错误!", user);

            }
            request.getSession().setAttribute(Constant.USERINFO_SESSION_KEY, user);
            return new ResponseBodyMessage<>(0, "登录成功!", user);
        }
    }
}
