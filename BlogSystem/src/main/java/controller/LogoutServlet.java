package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 首先找到当前会话
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.getWriter().write("当前用户尚未登录, 无法注销!");
            return;
        }
        // 如果登录了, 那就把当前的用户会话中的信息删掉就
        session.removeAttribute("user");
        // 302
        resp.sendRedirect("blog_login.html");
    }
}
