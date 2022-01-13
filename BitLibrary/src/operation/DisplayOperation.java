package operation;

import book.Book;
import book.BookList;

public class DisplayOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println("打印图书！");
        int size = bookList.getUsedSize();
        for (int i = 0; i < size; i++) {
            Book book = bookList.getPos(i);
            System.out.println(book);
        }
    }
}
