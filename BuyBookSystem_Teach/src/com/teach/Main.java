package com.teach;
import java.util.LinkedList;
import java.util.Scanner;
//主类
public class Main {
    //公共方法
    public static void showStar(String showTitle) {
        System.out.println("****************"+showTitle+"****************");
    }
    //主函数，程序开始后执行的地方
    public static void main(String[] args) {
        //书店
        Shop shop = new Shop();
        //打印提示可买的书的列表
        shop.printCanBuyBook();
        // 顾客
        Custom custom= new Custom();
        //顾客从图书中挑选购买书籍创建订单
        Order order =custom.buyBook(shop.getArrShopCanBuyBook());
        //打印订单信息
        order.printOrderInfo();
        //书店打印剩余书的信息
        showStar("shop left book");
        shop.printCanBuyBook();
    }


}
//书
class Book{
    //书的成员属性
    private int bookId;
    private String bookName;
    private double price;
    private int bookNumber;
    Book(int id, String name, double pc,int num){
        bookId =id;
        bookName =name;
        price = pc;
        bookNumber =num;
    }
    public int getBookId() {
        return bookId;
    }
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    public String getBookName() {
        return bookName;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getBookNumber() {
        return bookNumber;
    }
    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }
    // 打印书籍的基本信息
    void printBookInfo(){
        System.out.println("id:"+bookId+ " name:"+bookName+" price:"+price+" book number:"+bookNumber);
    }
}
//订单
class Order{
    //属性
    int orderId;
    Book[] arrBuyBookOrder;//已经购买的书
    double orderTotal;
    //构造方法
    Order(int id,Book[] bookList){
        orderId =id;
        arrBuyBookOrder =bookList;
        orderTotal =0.0;//初始默认值
        this.setTotal();//用函数自动计算订单的总额
    }
    //根据已经够买的书单计算总额
    void setTotal(){
        for (Book b: arrBuyBookOrder) {
            orderTotal+=(b.getPrice()*b.getBookNumber());
        }
    }
    //打印订单信息
    void printOrderInfo(){
        Main.showStar("Order Begin");
        System.out.println("orderId = " + orderId);

        for (Book b: arrBuyBookOrder) {
            b.printBookInfo();
        }
        System.out.println("orderTotal = " + orderTotal);
        Main.showStar("Order End");
    }
}
//顾客
class Custom{
    //顾客只有一个方法购买书
    public  Order buyBook(Book[] arrBookList){
        int intOrderID = 1;//订单编号
        Scanner scanner = new Scanner(System.in);//定义输入scanner
        short userInputId;//定义用户输入购买的id
        short userInputNum = 0;//定义用户输入的购买书的数量
        //定义用于暂存预购商品的购物车链表，因为不知道购买的几本书所以用链表来实现
        LinkedList<Book> lkShoppingCart = new LinkedList<>();
        while(true) {
            //while循环获取购买书的正确Id
            while(true) {
                System.out.print("please input book id which you will buy Or 0 to EXIT:");
                userInputId = scanner.nextShort();
                if(userInputId >=0 && userInputId<=arrBookList.length)//获取了正确值退出
                    break;
                else //获取错误值，重新再来
                    System.out.println("wrong id,try again...");
            }
            //用户输入0 退出 唯一出口，否则会不断的选择书
            if(userInputId == 0) break;
            //while循环获取购买书的正确数量
            while(true) {
                System.out.print("please input book number which you will buy:");
                userInputNum = scanner.nextShort();
                if(userInputNum>0 && userInputNum<=arrBookList[userInputId-1].getBookNumber())//获取了正确值退出
                    break;
                else//获取错误值，重新再来
                    System.out.println("wrong number,try again...");
            }
            //获取了正确合法的id和数量，将选购的书加入购物车
            Book selectBook = arrBookList[userInputId-1];//找到顾客选择的书
            //从书店书库取走顾客选购数量的书
            selectBook.setBookNumber(selectBook.getBookNumber()-userInputNum);
            //构建顾客选购的书的对象，将其加入顾客购物车数组
            lkShoppingCart.add(new Book(selectBook.getBookId(),selectBook.getBookName(),selectBook.getPrice(),userInputNum));
        }
        // 将购物车的书导入最终的购买书的数组中
        Book[] buyList = new Book[lkShoppingCart.size()] ;//定义最终购买书的数组，长度为购物车链表长度
        int i =0;
        //复制购物车中的书到购买数组中
        for (Book b:lkShoppingCart )
            buyList[i++]=b;
        //返回订单对象
        return new Order(intOrderID++,buyList);
    }
}
//商店
class Shop{
    //唯一的成员属性，直接给一个默认书单值简化程序
    private Book[] arrShopCanBuyBook = {
            new Book(1,"Java",78.9,100),
            new Book(2,"Math",32.9,50),
            new Book(3,"C++ ",56.9,70)
    };

    public Book[] getArrShopCanBuyBook() {
        return arrShopCanBuyBook;
    }
    public void setArrShopCanBuyBook(Book[] arrShopCanBuyBook) {
        this.arrShopCanBuyBook = arrShopCanBuyBook;
    }
    //打印输出可以供顾客否买的书
    void printCanBuyBook(){
        Main.showStar("shop book list");
        for (Book b: getArrShopCanBuyBook()) {
            b.printBookInfo();
        }
        Main.showStar("shop book end");
    }
}