
//整个程序入口的地方

import book.BookList;
import user.AdminUser;
import user.NormalUser;
import user.User;

import java.util.Scanner;

public class Main {

    public static User login() {
        System.out.println("请输入你的姓名：");
        Scanner scanner  = new Scanner(System.in);
        String name = scanner.nextLine();

        System.out.println("请输入你的身份：1>管理员 0>普通用户");
        int choice = scanner.nextInt();

        if(1 == choice) {
            return new AdminUser(name);
        } else {
            return new NormalUser(name);
        }
    }

    public static void main(String[] args) {
        BookList bookList = new BookList();
        User user = login();//向上转型
        while(true) {
            int choice = user.menu();//动态绑定
            //根据你的choice调用合适的操作
            user.doWork(choice, bookList);
        }
    }
}
