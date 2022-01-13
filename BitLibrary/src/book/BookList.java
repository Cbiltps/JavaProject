package book;

public class BookList {
    private Book[] books = new Book[10];
    private int usedSize;//放的几本书

    public BookList() { //BookList的构造方法
        books[0] = new Book("三国演义","罗贯中",17,"小说");
        books[1] = new Book("西游记","吴承恩",47,"小说");
        books[2] = new Book("水浒传","施耐庵",37,"小说");
        this.usedSize = 3;
    }

    public int getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(int usedSize) {
        this.usedSize = usedSize;
    }

    /**
     * 获取pos位置的一本书
     * @param pos
     * @return
     */
    public Book getPos(int pos) {
        return this.books[pos];//回到当前书的坐标
    }

    /**
     * 设置pos下标为一本书（添加一本书）
     * @param pos
     * @param book
     */
    public void setBooks(int pos, Book book) {
        this.books[pos] = book;//在当前位置放书
    }
}