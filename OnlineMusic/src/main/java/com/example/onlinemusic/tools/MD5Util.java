package com.example.onlinemusic.tools;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created with IntelliJ IDEA.
 * Description: 学习MD5加密
 * User: cbiltps
 * Date: 2023-05-17
 * Time: 00:01
 */
public class MD5Util {
    
    // 定义一个固定的盐值
    private static final String salt = "1b2i3t4e";


    /**
     * MD5加密方法
     * @param src 明文
     * @return
     */
    public static String MD5(String src) {
        return DigestUtils.md5Hex(src);
    }

    /**
     * 第一次加密: 模拟前端自己加密, 然后传到后端
     * @param inputPass
     * @return
     */
    public static String inputPassToFormPass(String inputPass) {
        String str = "" + salt.charAt(1) + salt.charAt(3) + inputPass + salt.charAt(5) + salt.charAt(6);
        return MD5(str);
    }

    /**
     * 第二次加密
     * @param formPass 前端加密过的密码, 传给后端进行第2次加密
     * @param salt     后端的当中的盐值
     * @return
     */
    public static String formPassToDBPass(String formPass, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return MD5(str);
    }

    /**
     * 封装上面两个方法
     * @param inputPass
     * @param saltDB
     * @return
     */
    public static String inputPassToDbPass(String inputPass, String saltDB) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println("对用户输入密码进行第1次加密: " + inputPassToFormPass("123456"));
        System.out.println("对用户输入密码进行第2次加密: " + formPassToDBPass(inputPassToFormPass("123456"), "1b2i3t4e"));
        System.out.println("对用户输入密码进行第2次加密: " + inputPassToDbPass("123456", "1b2i3t4e"));
    }
}
