package com.example.gobang.api;

import com.example.gobang.model.User;
import com.example.gobang.model.UserMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-04-23
 * Time: 00:51
 */
@RestController
public class UserAPI {
    @Resource
    private UserMapper userMapper;

    @PostMapping("/login")
    public Object login(String username, String password, HttpServletRequest request) {
        // 关键操作: 就是根据username去数据库中进行查询, 如果能找到匹配的用户并且密码一致, 登录成功.
        User user = userMapper.selectByName(username);
        System.out.println("[login] : " + user.getUsername());
        if (user == null || !user.getPassword().equals(password)) {
            System.out.println("登录失败!");
            return new User();
        }
        // 不仅要验证登录, 并且要将身份信息保存至会话中
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("user", user);
        return user;
    }

    @PostMapping("/register")
    public Object register(String username, String password) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            userMapper.insert(user);
            return user;
        } catch (DuplicateKeyException e) { // 用户名不能重复, 重复既返回一个空对象
            return new User();
        }
    }

    @GetMapping("/userinfo")
    public Object getUserInfo(HttpServletRequest request) {
        // 可以不用在数据库里查, 直接查会话信息就可以
        try {
            HttpSession httpSession = request.getSession(false);
            User user = (User) httpSession.getAttribute("user"); // 默认是Object类型
            return user;
        } catch (NullPointerException e) {
            return new User();
        }
    }
}
