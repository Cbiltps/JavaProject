package com.company;

import java.util.Scanner;

public class StudentVote {

    public static void main(String[] args) {

        // 郝同学总
        int hCount = 0;
        // 刘同学总
        int lCount = 0;
        // 总票数
        int count = 0;

        Scanner scanner = new Scanner(System.in);
        //遍历
        for (int i = 0; i < 10; i++) {
            System.out.println("请投票-> 1:郝 2:刘");
            String name = scanner.nextLine();
            if (name.equals("1")) {
                hCount++;
            } else if (name.equals("2")) {
                lCount++;
            }
            System.out.println("感谢你的投票");
            count = hCount + lCount;
        }
        if (hCount > lCount) {
            System.out.println("一共" + count + "票，郝同学" + hCount + "票，恭喜当选优秀干部！");
        } else if (hCount < lCount) {
            System.out.println("一共" + count + "票，刘同学" + lCount + "票，恭喜当选优秀干部！");
        } else {
            System.out.println("票数一样，重新投票");
        }
    }
}
