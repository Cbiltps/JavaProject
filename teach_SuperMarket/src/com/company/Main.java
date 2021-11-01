package com.company;

//import org.jetbrains.annotations.NotNull;

import com.sun.istack.internal.NotNull;

import javax.xml.bind.SchemaOutputResolver;
import java.util.LinkedList;
import java.util.Scanner;


public class Main {
    public static int st_order_id=1;
    public static int st_goods_id=1;
    public static void printStar(String info){
        System.out.println("******************"+info+"******************");
    }
    public static void main(String[] args) {
        Market market = new Market("华润万家");
        market.welcome();

        Customer customer = new Customer("小明");
        Order order = customer.buyGoods(market,market.getArrMarketGoodsList());
        order.printOrderList();
        market.welNext();
    }

}
class Goods{
    int goodsId;
    String goodsName;
    double goodsPrice;
    int goodsNumber;
    public int getGoodsId() {
        return goodsId;
    }
    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }
    public String getGoodsName() {
        return goodsName;
    }
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
    public double getGoodsPrice() {
        return goodsPrice;
    }
    public void setGoodsPrice(double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }
    public int getGoodsNumber() {
        return goodsNumber;
    }
    public void setGoodsNumber(int goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public Goods(String goodsName, double goodsPrice, int goodsNumber) {
        this.goodsId = Main.st_goods_id++;
        this.goodsName = goodsName;
        this.goodsPrice = goodsPrice;
        this.goodsNumber = goodsNumber;
    }

    public Goods(int goodsId, String goodsName, double goodsPrice, int goodsNumber) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.goodsPrice = goodsPrice;
        this.goodsNumber = goodsNumber;
    }

    public void printGoodsInfo(){
        if(this.getGoodsNumber() > 0)
            System.out.println("id:"+getGoodsId()+" name:"+getGoodsName()+" price:"+getGoodsPrice()+" number:"+getGoodsNumber());
    }
}
class Market{
    String marketName;
    Goods[] arrMarketGoodsList={
            new Goods("apple ",5.99,1000),
            new Goods("pear  ",6.99,1000),
            new Goods("banana",4.99,1000)
    };

    public String getMarketName() {
        return marketName;
    }
    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }
    public Goods[] getArrMarketGoodsList() {
        return arrMarketGoodsList;
    }
    public void setArrMarketGoodsList(Goods[] arrMarketGoodsList) {
        this.arrMarketGoodsList = arrMarketGoodsList;
    }
    public Market(String marketName) {
        this.marketName = marketName;
    }
    void printMarketGoodsInfo(){
        Main.printStar(getMarketName()+" 商品列表");
        for (int i = 0; i <arrMarketGoodsList.length; i++)
            arrMarketGoodsList[i].printGoodsInfo();
    }
    void welcome(){
        Main.printStar("欢迎光临 "+getMarketName());
    }
    void welNext(){
        Main.printStar("欢迎再来 "+getMarketName());
    }

}

class Customer{
    String customerName;
    public String getCustomerName() {
        return customerName;
    }
    public Customer(String customerName) {
        this.customerName = customerName;
    }

    Order buyGoods(@NotNull Market market, Goods[] arrCanBuyGoods){
        Scanner scanner = new Scanner(System.in);
        LinkedList<Goods> lkMarketCart = new LinkedList<>();
        int inputId;
        int inputNum=0;
        Goods selectGoods = null;


        while (true){
            boolean isSame = false;
            market.printMarketGoodsInfo();
            while(true) {
                System.out.print("请选择要购买的商品id 或者 按0退出=");
                inputId = scanner.nextInt();
                if(inputId >= 0 && inputId <= arrCanBuyGoods.length )
                    break;
                else
                    System.out.println("错误ID，请重新输入...");
            }
            if(inputId==0) break;
            selectGoods = arrCanBuyGoods[inputId - 1];
            if(selectGoods.getGoodsNumber()==0){
                System.out.println("选择商品已经售罄，请重新选择其它商品...");
                continue;
            }
            while(true){
                System.out.print("请选择要购买"+selectGoods.getGoodsName()+"的数量=");
                inputNum = scanner.nextInt();
                if(inputNum > 0 && inputNum <= selectGoods.getGoodsNumber() )
                    break;
                else
                    System.out.println("输入数量错误，请重新输入...");
            }
            selectGoods.setGoodsNumber(selectGoods.getGoodsNumber()-inputNum);
            if(!lkMarketCart.isEmpty()) {
                for (Goods g : lkMarketCart) {
                    if (selectGoods.getGoodsName() == g.getGoodsName()) {
                        g.setGoodsNumber(g.getGoodsNumber() + inputNum);
                        isSame = true;
                        break;
                    }

                }
            }
            if(!isSame)
                lkMarketCart.add(new Goods(selectGoods.getGoodsId(), selectGoods.getGoodsName(), selectGoods.getGoodsPrice(), inputNum));
            System.out.println("【您购买了数量为"+inputNum+"的"+selectGoods.getGoodsName()+"】");
        }
        Goods[] customerBuyGoodsList = new Goods[lkMarketCart.size()];
        int i = 0;
        for (Goods g:lkMarketCart)
            customerBuyGoodsList[i++]=g;
        return new Order(customerBuyGoodsList);
    }
}
class Order{
    private int orderId;
    private Goods[] arrOrderBuyGoodsList;
    private double orderTotal;

    public Order(Goods[] orderBuyGoodsList) {
        this.orderId = Main.st_order_id++;
        this.arrOrderBuyGoodsList = orderBuyGoodsList;
        setOrderTotal();
    }

    public int getOrderId() {
        return orderId;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal() {
        double total=0.0;
        for (Goods goods:arrOrderBuyGoodsList)
            total+=goods.getGoodsPrice()*goods.getGoodsNumber();
        this.orderTotal = total;
    }
    void printOrderList(){
        System.out.println("Order ID="+getOrderId());
        for (Goods g:arrOrderBuyGoodsList)
            g.printGoodsInfo();
        System.out.println("Order Total="+getOrderTotal());
    }
}
