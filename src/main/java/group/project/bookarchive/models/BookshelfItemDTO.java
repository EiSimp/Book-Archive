package group.project.bookarchive.models;

public class BookshelfItemDTO {
    private BookshelfDTO bookshelf;
    private BookDTO book;
    private String userComment;
    private double userRating;

    public BookshelfItemDTO(Bookshelf bookshelf, Book book, BookshelfItem item) {
        this.bookshelf = new BookshelfDTO(bookshelf);
        this.book = new BookDTO(book);
        this.userComment = item.getUserComment();
        this.userRating = item.getUserRating();

    }

    public BookshelfDTO getBookshelf() {
        return this.bookshelf;
    }

    public void setBookshelf(BookshelfDTO bookshelf) {
        this.bookshelf = bookshelf;
    }

    public BookDTO getBook() {
        return this.book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    public String getUserComment() {
        return this.userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public double getUserRating() {
        return this.userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

}
