package com.company;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Shop shop = new Shop("Apple Store");
        shop.welcome();
        shop.showShopPhone();
        shop.sellPhone();
        shop.orderPhone();
        shop.welNext();
    }
}

class Shop{
    String shopName;
    Phone[] phoneList = {
            new Phone(1,"Iphone13",8888),
            new Phone(2,"Iphone12",7777),
            new Phone(3,"Iphone11",6666)
    };

    public Shop(String shopName) {
        this.shopName = shopName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    void orderPhone() {
        System.out.println("**********Phone Order***********");
//        System.out.println("You bought "+this.+"fruits");

    }

    void sellPhone() {
        System.out.println();

        int inputId;
        int inputNum;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please select the ID you need or select 0 to exit system...");
            while (true) {
                inputId = scanner.nextInt();
                if(inputId >= 0 && inputId <= 3) {
                    break;
                }else {
                    System.out.println("Error, please re-enter...");
                }
            }

            if(inputId == 0) { //输入0跳出循环
                break;
            }

            while (true) {
                System.out.println("Enter the quantity you need...");
                inputNum = scanner.nextInt();
                if(inputNum >= 0) {
                    break;
                }else {
                    System.out.println("Error, please re-enter...");
                }
            }
        }


    }

    void showShopPhone() {
        System.out.println();
        System.out.println("**********Phone List***********");
        for (Phone a : phoneList) {
            a.showPhone();
        }
    }

    void welcome(){
        System.out.println("Welcome to the "+this.getShopName()+"!");
    }
    void welNext(){
        System.out.println("Welcome to the "+this.getShopName()+"next time!");
    }
}

class Phone {
    int phoneID;
    String phoneName;
    double phonePrice;
    int phoneNum = 0;

    public Phone(int phoneID, String phoneName, double phonePrice) {
        this.phoneID = phoneID;
        this.phoneName = phoneName;
        this.phonePrice = phonePrice;
    }

    public int getPhoneID() {
        return phoneID;
    }
    public void setPhoneID(int phoneID) {
        this.phoneID = phoneID;
    }
    public String getPhoneName() {
        return phoneName;
    }
    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }
    public double getPhonePrice() {
        return phonePrice;
    }
    public void setPhonePrice(double phonePrice) {
        this.phonePrice = phonePrice;
    }
    public int getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(int phoneNum) {
        this.phoneNum = phoneNum;
    }

    void showPhone(){
            System.out.println("ID:"+this.getPhoneID()+" Name:"+this.getPhoneName()+" Price:"+this.getPhonePrice());
    }
}