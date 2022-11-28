package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 这个类用于封装 博客表 的基本操作
public class BlogDao {
    // 1. 往博客表里, 插入一个博客
    public void insert(Blog blog) {
        // JDBC基本代码~~
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // 1) 和数据库建立连接.
            connection = DBUtil.getConnection();
            // 2) 构造 SQL 语句
            String sql = "insert into blog values(null, ?, ?, ?, now())";
            statement = connection.prepareStatement(sql);
            statement.setString(1, blog.getTitle());
            statement.setString(2, blog.getContent());
            statement.setInt(3, blog.getUserId());
            // 3) 执行 SQL
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 4) 关闭连接, 释放资源
            DBUtil.close(connection, statement, null);
        }
    }

    // 2. 能够获取到博客表中的所有博客的信息 (用于在博客列表页, 此处每篇博客不一定会获取到完整的正文)
    public List<Blog> selectAll() {
        List<Blog> blogs  = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            // 下面的代码一定要排序, 在数据没有 order by 之前, 不要依赖数据的顺序!!!
            // mysql并没有规定数据的顺序就是按照插入的顺序定制的!!!
            String sql = "select * from blog order by postTime desc";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Blog blog = new Blog();
                blog.setBlogId(resultSet.getInt("blogId"));
                blog.setTitle(resultSet.getString("title"));
                // 这里需要针对内容进行截断(太长了,就需要截断)
                String content = resultSet.getString("content");
                if (content.length() > 50) {
                    content = content.substring(0, 50) + "...";
                }
                blog.setContent(content);
                blog.setUserId(resultSet.getShort("userId"));
                blog.setPostTime(resultSet.getTimestamp("postTime"));
                blogs.add(blog);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, resultSet);
        }
        return blogs;
    }

    // 3. 能够根据博客 id 获取到指定的博客内容 (用于在博客详情页)
    public Blog selectOne(int blogId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "select * from blog where blogId = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, blogId);
            resultSet = statement.executeQuery();
            // 此处我们是使用 主键 来作为查询条件的, 查询结果要么是 1, 要么是 0
            if (resultSet.next()) {
                Blog blog = new Blog();
                blog.setBlogId(resultSet.getInt("blogId"));
                blog.setTitle(resultSet.getString("title"));
                blog.setContent(resultSet.getString("content"));
                blog.setUserId(resultSet.getShort("userId"));
                blog.setPostTime(resultSet.getTimestamp("postTime"));
                return blog;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, resultSet);
        }
        return null;
    }

    // 4. 从博客表中, 根据博客 id 删除博客
    public void delete(int blogId) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "delete from blog where blogId = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, blogId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement ,null);
        }
    }

    // 注意, 上述操作是 增删查, 没有改~~
}
