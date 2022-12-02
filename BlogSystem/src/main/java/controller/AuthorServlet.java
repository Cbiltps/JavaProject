package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Blog;
import model.BlogDao;
import model.User;
import model.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/authorInfo")
public class AuthorServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf8");
        String blogIdStr = req.getParameter("blogId");
        if (blogIdStr == null || "".equals(blogIdStr)) {
            // 没有这个ID 请求中的参数有问题
            resp.getWriter().write("{\"OK\": false, \"reason\": 参数缺失!}");
            return;
        }

        // 根据当前blogId查找Blog对象,根据Blog对象找到对应的作者信息
        BlogDao blogDao = new BlogDao();
        Blog blog = blogDao.selectOne(Integer.parseInt(blogIdStr));
        if (blog == null) {
            // 没有这篇博客
            resp.getWriter().write("{\"OK\": false, \"reason\": 没有这篇博客!}");
            return;
        }

        // 根据Blog对象找到对应的作者信息(用户对象)
        UserDao userDao = new UserDao();
        User author = userDao.selectById(blog.getUserId());
        if (author == null) {
            resp.getWriter().write("{\"OK\": false, \"reason\": 没有这位作者!}");
            return;
        }

        // 把 author 返回给浏览器
        // 注意:不要返回密码
        author.setPassword("");
        resp.getWriter().write(objectMapper.writeValueAsString(author));
    }
}
