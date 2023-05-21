package com.example.onlinemusic.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cbiltps
 * Date: 2023-05-20
 * Time: 17:14
 */
public class TestTime {
    public static void main(String[] args) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sdf.format(new Date());
        System.out.println("当前的时间: " + time);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time2 = sdf2.format(new Date());
        System.out.println("当前的时间: " + time2);

    }
}
