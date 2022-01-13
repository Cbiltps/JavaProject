package operation;

import book.Book;
import book.BookList;

public class BorrowOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println("借阅图书！");
        System.out.println("请输入书名：");
        String name = scanner.nextLine();
        int size = bookList.getUsedSize();
        for (int i = 0; i < size; i++) {
            Book book = bookList.getPos(i);
            if(name.equals(book.getName())) {
                book.setBorrowed(true);
                System.out.println("借阅成功！");
                return;
            }
        }
        System.out.println("没有你要借的书！");
    }
}
