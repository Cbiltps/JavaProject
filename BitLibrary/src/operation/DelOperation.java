package operation;

import book.Book;
import book.BookList;

public class DelOperation implements IOperation {
    @Override
    public void work(BookList bookList) {
        System.out.println("删除图书！");
        System.out.println("请输入书名：");
        String name = scanner.nextLine();
        int currentSize = bookList.getUsedSize();
        int index = 0;//存储删除书的下标
        int i = 0;

        //1：找到被删书的下标
        for (; i < currentSize; i++) {
            Book book = bookList.getPos(i);
            if (name.equals(book.getName())) {
               index = i;//成功获取
               break;
            }
        }

        if (i >= currentSize) {
            System.out.println("没有此书！");
            return;
        }

        //2：删除
        for (int j = index; j < currentSize -1; j++) {
            Book book = bookList.getPos(j + 1);
            bookList.setBooks(j, book);//把 j+1 下标的书给 j 下标
        }
        bookList.setBooks(currentSize, null);//最后一个元素置空
        bookList.setUsedSize(currentSize - 1);//数量减1
        System.out.println("删除完成！");
    }
}
