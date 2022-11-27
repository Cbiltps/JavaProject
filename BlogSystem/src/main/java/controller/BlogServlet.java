package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Blog;
import model.BlogDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// 通过这个类, 来处理 /blog 路径对应的请求
@WebServlet("blog")
public class BlogServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();

    // 这个方法用来获取到数据库中的博客列表
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BlogDao blogDao = new BlogDao();
        List<Blog> blogs = blogDao.selectAll();
        // 把 blogs 对象转成 JSON 格式.
        String respJson = objectMapper.writeValueAsString(blogs);
        resp.setContentType("application/json; charset=utf8");
        resp.getWriter().write(respJson);
    }
}
