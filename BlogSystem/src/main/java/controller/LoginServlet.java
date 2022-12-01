package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;
import model.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf8");
        resp.setCharacterEncoding("utf8");
        // 1. 获取请求中的参数
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        System.out.println("username=" + username + ", password=" + password);
        if (username == null || "".equals(username) || password == null || "".equals(password)) {
            // 请求的内容缺失, 肯定是登录失败!!
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("当前的用户名或密码为空!");
            return;
        }

        // 2. 和数据库中的内容进行比较
        UserDao userDao = new UserDao();
        User user = userDao.selectByName(username);
        if (user == null || !user.getPassword().equals(password)) {
            resp.setContentType("text/html; charset=utf8");
            resp.getWriter().write("用户名或密码错误!");
            return;
        }

        // 3. 如果比较通过就创建会话
        HttpSession session = req.getSession(true);
        // 把刚才的用户信息, 存储到会话中.
        session.setAttribute("user", user);
        // 4. 返回一个重定向报文, 跳转到博客列表页.
        resp.sendRedirect("blog_list.html");
    }

    // 这个方法用来让前端检测当前的登录状态.
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf8");
        HttpSession session = req.getSession(false);
        if (session == null) {
            // 检测下会话是否存在, 不存在说明未登录!
            User user = new User();
            resp.getWriter().write(objectMapper.writeValueAsString(user));
            return;
        }
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // 虽然有会话, 但是会话里没有 user 对象, 也视为未登录.
            user = new User();
            resp.getWriter().write(objectMapper.writeValueAsString(user));
            return;
        }

        // 已经登录的状态!!
        // 注意, 此处不要把密码给返回到前端~~
        user.setPassword("");
        resp.getWriter().write(objectMapper.writeValueAsString(user));
    }
}
