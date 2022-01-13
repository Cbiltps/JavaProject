package operation;

import book.Book;
import book.BookList;

public class FindOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println("查找图书！");
        String name = scanner.nextLine();
        int size = bookList.getUsedSize();
        for (int i = 0; i < size; i++) {
            Book book = bookList.getPos(i);
            if(name.equals(book.getName())) {
                System.out.println("找到了，信息如下：");
                System.out.println(book);
                return;
            }
        }
        System.out.println("没找到！");
    }
}
