import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Message {
    public String from;
    public String to;
    public String message;
}

@WebServlet("/message")
public class MessageServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<Message> messages = new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 处理提交信息请求
        Message message = objectMapper.readValue(req.getInputStream(), Message.class);
        // 最简单的保存方法就是保存到内存中
        messages.add(message);
        // 通过ContentType来告知页面, 返回的数据是json格式
        // 有了这样的声明, 此时 jquery ajax 就会自动的帮我们把字符串转成 js 对象
        // 如果没有, jquery ajax 就只是当成字符串来处理的~~
        resp.setContentType("application/json; charset=utf8");
        resp.getWriter().write("{\"ok\": true}");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取到消息列表, 只要把消息列表中的内容整个的都返回给客户端即可
        // 此处需要使用 ObjectMapper 把 Java 对象, 转成 JSON 格式字符串~
        String jsonString = objectMapper.writeValueAsString(messages);
        System.out.println("jsonString: " + jsonString);
        resp.setContentType("application/json; charset=utf8");
        resp.getWriter().write(jsonString);
    }
}
