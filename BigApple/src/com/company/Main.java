package com.company;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Shop shop = new Shop("Big Apple Shop");
        shop.welcome();
        shop.purchase();//进货方法
        shop.showShopApple();
        shop.sellApple();
        shop.orderApple();
        shop.welNext();
    }
}

class Shop{
    String shopName;

    Apple apple = new Apple(22332,"陕西大苹果",1.5);

    int inputId;
    int inPurchase;
    int inputNum;

    public Shop(String shopName) {
        this.shopName = shopName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    void orderApple() {
        System.out.println("**********Apple Order***********");
        System.out.println("您买了"+inputNum+"个苹果，价格为"+inputNum*1.5+"元！");
        System.out.println("现在仓库还有"+(inPurchase - inputNum)+"个苹果！");
    }

    void purchase() {
        System.out.println();
        System.out.println("************现在开始进货************");
        System.out.println("请输入进货数...");
        Scanner scanner = new Scanner(System.in);
        inPurchase = scanner.nextInt();
        System.out.println("您进了"+inPurchase+"个苹果！");

    }


    void sellApple() {
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please select the ID you need...");
            while (true) {
                inputId = scanner.nextInt();
                if(22332 == inputId) {
                    break;
                }else {
                    System.out.println("Error, please re-enter...");
                }
            }

            System.out.println("Enter the quantity you need...");
            inputNum = scanner.nextInt();
            if(inputNum >= 0) {
                break;
            }else {
                System.out.println("Error, please re-enter...");
            }
        }
    }

    void showShopApple() {
        System.out.println();
        System.out.println("**********Apple List***********");
        System.out.println("苹果库存："+inPurchase);
        apple.showApple();
    }

    void welcome(){
        System.out.println("Welcome to the "+this.getShopName()+"!");
    }
    void welNext(){
        System.out.println("Welcome to the "+this.getShopName()+"next time!");
    }
}

class Apple {
    int appleID;
    String appleName;
    double applePrice;
    int appleNum = 0;

    public Apple(int appleID, String appleName, double applePrice) {
        this.appleID = appleID;
        this.appleName = appleName;
        this.applePrice = applePrice;
    }

    public int getAppleID() {
        return appleID;
    }

    public void setAppleID(int appleID) {
        this.appleID = appleID;
    }

    public String getAppleName() {
        return appleName;
    }

    public void setAppleName(String appleName) {
        this.appleName = appleName;
    }

    public double getApplePrice() {
        return applePrice;
    }

    public void setApplePrice(double applePrice) {
        this.applePrice = applePrice;
    }

    public int getAppleNum() {
        return appleNum;
    }

    public void setAppleNum(int appleNum) {
        this.appleNum = appleNum;
    }

    void showApple(){
        System.out.println("ID:"+this.getAppleID()+" Name:"+this.getAppleName()+" Price:"+this.getApplePrice());
    }
}