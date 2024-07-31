
package group.project.bookarchive.models;

public class BookDTO {

    private String googleBookId;
    private String title;
    private String bookId;
    private double averageRating;
    private String thumbnail;
    private String author;
    private String category;

    public BookDTO() {
    }

    public BookDTO(String title, String bookId, double averageRating, String thumbnail, String googleBookId) {
        this.title = title;
        this.bookId = bookId;
        this.averageRating = averageRating;
        this.thumbnail = thumbnail;
        this.googleBookId = googleBookId;
    }

    public BookDTO(Book book) {
        this.googleBookId = book.getGoogleBookId();
        this.title = book.getTitle();
        this.bookId = book.getId().toString();
        this.averageRating = book.getAverageRating();
        this.thumbnail = book.getThumbnailUrl();
        this.author = book.getAuthor();
        this.category = book.getCategory();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAverageRating() {
        return this.averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBookId() {
        return this.bookId;
    }

    public void setBookID(String bookId) {
        this.bookId = bookId;
    }

    public String getGoogleBookId() {
        return this.googleBookId;
    }

    public void setGoogleBookID(String googleBookId) {
        this.googleBookId = googleBookId;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
