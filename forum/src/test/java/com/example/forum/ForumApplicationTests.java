package com.example.forum;

import com.example.forum.dao.UserMapper;
import com.example.forum.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class ForumApplicationTests {
    // 数据源
    @Resource
    private DataSource dataSource;

    // 用户的Mapper
    @Resource
    private UserMapper userMapper;

    @Test
    void testConnection() throws SQLException {
        System.out.println("dataSource = "+ dataSource.getClass());
        // 获取数据库连接
        Connection connection = dataSource.getConnection();
        System.out.println("connection = "+ connection);
        connection.close();

    }

    @Test
    void testMybatis() {
        User user = userMapper.selectByPrimaryKey(1L);
        System.out.println(user);
        System.out.println(user.getUsername());
    }
    @Test
    void contextLoads() {
        System.out.println("TEST: spring");
    }

}
