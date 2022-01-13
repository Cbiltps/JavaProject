package operation;

import book.Book;
import book.BookList;

public class AddOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println("新增图书！");
        System.out.println("请输入书名：");
        String name = scanner.nextLine();
        System.out.println("请输入作者：");
        String author = scanner.nextLine();
        System.out.println("请输入类型：");
        String type = scanner.nextLine();
        System.out.println("请输入价格：");
        int price = scanner.nextInt();
        Book book = new Book(name,author,price,type);

        int size = bookList.getUsedSize();
        bookList.setBooks(size, book);
        bookList.setUsedSize(size + 1);

        System.out.println("新增图书成功！");
    }
}
