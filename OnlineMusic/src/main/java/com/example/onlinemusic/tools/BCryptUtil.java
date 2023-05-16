package com.example.onlinemusic.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created with IntelliJ IDEA.
 * Description: BCrypt加密
 * User: cbiltps
 * Date: 2023-05-17
 * Time: 01:06
 */
public class BCryptUtil {
    public static void main(String[] args) {
        // 模拟从前端获得的密码
        String password = "123456";
        
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // 使用 encode 方法进行加密
        String newPassword = bCryptPasswordEncoder.encode(password);
        System.out.println("加密的密码为: " + newPassword);

        // 使用 matches 方法进行密码的校验
        boolean samePasswordResult = bCryptPasswordEncoder.matches(password, newPassword);
        System.out.println("加密的密码和正确密码对比结果: " + samePasswordResult);

        // 使用 matches 方法进行密码的校验
        boolean otherPasswordResult = bCryptPasswordEncoder.matches("987654", newPassword);
        System.out.println("加密的密码和错误的密码对比结果: " + otherPasswordResult);
    }
}
