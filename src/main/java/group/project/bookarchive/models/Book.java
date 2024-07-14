package group.project.bookarchive.models;

public class Book {
    private String title;
    private double averageRating;
    private String thumbnail;

    public Book() {
    }

    public Book(String title, double averageRating, String thumbnail) {
        this.title = title;
        this.averageRating = averageRating;
        this.thumbnail = thumbnail;
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

}
