//
//实现一个简单的随机点名器
//1.定义数组，存储学生姓名
//2.预览数组，预览学生姓名
//3.随机数组，随机抽取一个学生姓名
//

//这一段是学习日志
//看到题目的时候我肚子里空空...
//然后在CSDN学习了一下，原来很简单，需引用java.util.Random就可以
//当然了，我改了很多东西，由于时间问题，没弄多次点名！
//直接贴学习地址：https://blog.csdn.net/giggs_123/article/details/79142246?spm=1001.2014.3001.5501
//现在对Java的基本情况有所了解，但是还是要上一下博哥的课，不然脑子里没有清晰的认识!



package com.company;

import java.util.Random;

public class RollCall {

    public static void main(String[] args) {

        String [] names = new String [10];//数组的定义
        ResStudents(names);
        PrintStudents(names);
        System.out.println("随机抽取的学生为：" + RandomStudents(names));
        System.out.println("————————————————————");
    }

    //储存学生的姓名
    public static void ResStudents(String[] names) {
        names[0] = "小艾";//初始化数组,这样写就是为了看的清楚
        names[1] = "小花";
        names[2] = "小明";
        names[3] = "小雷";
        names[4] = "小凯";
        names[5] = "小段";
        names[6] = "小王";
        names[7] = "小李";
        names[8] = "小霸王";
        names[9] = "老不死";
    }

    //预览学生姓名
    public static void PrintStudents(String[] names) {
        System.out.println("—————————");
        System.out.println("学生名单：");
        for(int i = 0; i < names.length; i++) {
             System.out.println(names[i]);
        }
        System.out.println("————————————————————");
    }

    //随机抽取姓名
    public static String RandomStudents(String[] names) {
        Random rand = new Random();
        int Lucky = rand.nextInt(names.length);//抽取幸运观众
        return names[Lucky];
    }
}
