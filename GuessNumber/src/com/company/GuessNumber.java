

//这一段是学习日志
//其实在写Java的猜数字游戏之前，我还写了C语言版本的猜数字游戏！
//我直接把GitHub链接贴上：https://github.com/Cbiltps/Project
//
// 难点：
//1：随机数的生成———Java相对于C来说比较有好，就是下一段代码，但是听说缺点是随机数可以预测？？？
//本来想直接引一个时间戳的，但是由于时间问题就先勉为其难的用了。。。
//int RandomNumber = (int) (Math.random() * 100 + 1);
//2：虽然是实现了下面的功能但是这段代码还是太挫了，像狗爬的一样，后期我会优化一下并传到GitHub上
//3；关于代码风格，我觉的很不好，我想请教一下！这一点非常的难受，阿里巴巴的Java规范我有，但是Java的基础太差了！



//
//猜数字游戏
//1：电脑随机生成一个1—100之间的数字
//2：玩家猜数字
//   如果猜对了，就提示：猜对了
//   如果猜错了，就提示：猜大了，或者猜小了，知道猜正确
//3：反复玩
//



package com.company;

import java.util.Scanner;//导入包中的Scanner类

public class GuessNumber {

    public static void main(String[] args) {
        do{
            Menu();
            System.out.print("请选择:>");
            Scanner scan = new Scanner(System.in);
            int input = scan.nextInt();
            switch (input) {
                case 1:
                    Game();
                    break;
                case 0:
                    System.out.println("退出游戏");
                    System.exit(0);
                default:
                    System.out.println("选择错误，重新选择");
                    break;
            }
        } while(true);
    }

    public static void Menu() {
        System.out.println("**************************************");
        System.out.println("*************  1.play  ***************");
        System.out.println("*************  0.exit  ***************");
        System.out.println("**************************************");
    }

    public static void Game() {
        int RandomNumber = (int) (Math.random() * 100 + 1);//生成1到100的随机数
        while (true) {
            System.out.print("请猜数字:>");
            Scanner sc = new Scanner(System.in);
            int GuessNumber = sc.nextInt();
            if (GuessNumber < RandomNumber) {
                System.out.println("猜小了");
            }
            else if (GuessNumber > RandomNumber) {
                System.out.println("猜大了");
            }
            else {
                System.out.println("恭喜你，猜对了");
                break;
            }
        }
    }
}
