package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Blog;
import model.BlogDao;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

// 通过这个类, 来处理 /blog 路径对应的请求
@WebServlet("/blog")
public class BlogServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();

    // 这个方法用来获取到数据库中的博客列表
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BlogDao blogDao = new BlogDao();

        // 先尝试获取到 req 中的 blogId 参数. 如果该参数存在, 说明是要请求博客详情
        // 如果该参数不存在, 说明是要请求博客的列表.
        String blogIdStr = req.getParameter("blogId");
        if (blogIdStr == null) {
            // 不存在参数, 获取博客列表
            List<Blog> blogs = blogDao.selectAll();
            // 把 blogs 对象转成 JSON 格式.
            String respJson = objectMapper.writeValueAsString(blogs);
            resp.setContentType("application/json; charset=utf8");
            resp.getWriter().write(respJson);
        } else {
            // 存在参数, 获取博客详情页
            int blogId = Integer.parseInt(blogIdStr);
            Blog blog = blogDao.selectOne(blogId);
            String respJson = objectMapper.writeValueAsString(blog);
            resp.setContentType("application/json; charset=utf8");
            resp.getWriter().write(respJson);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            // 当前用户未登录, 不能提交博客!
            resp.setContentType("text/html;charset=utf8");
            resp.getWriter().write("当前用户未登录, 不能提交博客!");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // 当前用户未登录, 不能提交博客!
            resp.setContentType("text/html;charset=utf8");
            resp.getWriter().write("当前用户未登录, 不能提交博客!");
            return;
        }

        // 一定要先指定好请求按照哪种编码来解析
        req.setCharacterEncoding("utf8");
        // 先从请求中, 取出参数(博客的标题和正文)
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        if (title == null || "".equals(title) || content == null || "".equals(content)) {
            // 直接告诉客户端, 请求参数不对
            resp.getWriter().write("提交博客失败! 缺少必要的参数!");
            return;
        }

        // 构造 Blog 对象, 把当前的信息填进去, 并插入数据库中
        // 此处要给 Blog 设置的属性, 主要是 title, content, userId (作者信息)
        // postTime 和 blogId 都不需要手动指定, 都是插入数据库的时候自动生成的.
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        // 作者 id 就是当前提交这个博客的用户的身份信息!!
        blog.setUserId(user.getUserId());
        BlogDao blogDao = new BlogDao();
        blogDao.insert(blog);
        // 重定向到, 博客列表页!
        resp.sendRedirect("blog_list.html");
    }
}
