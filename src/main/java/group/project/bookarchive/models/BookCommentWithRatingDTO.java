package group.project.bookarchive.models;

public class BookCommentWithRatingDTO {
    private BookComment comment;
    private Double rating;

    public BookCommentWithRatingDTO(BookComment comment, Double rating) {
        this.comment = comment;
        this.rating = rating;
    }

    // Getters and Setters
    public BookComment getComment() { return comment; }
    public void setComment(BookComment comment) { this.comment = comment; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
}