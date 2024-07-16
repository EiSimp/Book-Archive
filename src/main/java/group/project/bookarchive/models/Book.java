package group.project.bookarchive.models;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String googleBookId;

    @Column(length = 255)
    private String title;

    @Column(length = 255)
    private String author;

    @Column(length = 255)
    private String publisher;

    @Column(length = 5000)
    private String thumbnailUrl;

    @Column(length = 5000)
    private String biggerThumbnailUrl;

    private double averageRating;

    @Column(length = 50)
    private String publishedDate;

    @Column(length = 20)
    private String isbn;

    @Column(length = 255)
    private String category;

    @Column(length = 5000)
    private String description;

    @Column(length = 5000)
    private String authorDescription;

    public Book() {
    }

    public Book(String googleBookId, String title, String author, String publisher, String thumbnailUrl,
            String biggerThumbnailUrl, double averageRating, String publishedDate, String isbn,
            String category, String description, String authorDescription) {
        this.googleBookId = googleBookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.thumbnailUrl = thumbnailUrl;
        this.biggerThumbnailUrl = biggerThumbnailUrl;
        this.averageRating = averageRating;
        this.publishedDate = publishedDate;
        this.isbn = isbn;
        this.category = category;
        this.description = description;
        this.authorDescription = authorDescription;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoogleBookId() {
        return googleBookId;
    }

    public void setGoogleBookId(String googleBookId) {
        this.googleBookId = googleBookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getBiggerThumbnailUrl() {
        return biggerThumbnailUrl;
    }

    public void setBiggerThumbnailUrl(String biggerThumbnailUrl) {
        this.biggerThumbnailUrl = biggerThumbnailUrl;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorDescription() {
        return authorDescription;
    }

    public void setAuthorDescription(String authorDescription) {
        this.authorDescription = authorDescription;
    }
}
