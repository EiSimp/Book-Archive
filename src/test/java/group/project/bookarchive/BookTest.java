package group.project.bookarchive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import group.project.bookarchive.models.Book;

public class BookTest {

    @Test
    public void testFullConstructor() {
        Book book = new Book(
            "googleBookId123", 
            "The Great Gatsby", 
            "F. Scott Fitzgerald", 
            "Scribner", 
            "http://example.com/thumbnail.jpg", 
            "http://example.com/bigger-thumbnail.jpg", 
            4.7, 
            "1925-04-10", 
            "9780743273565", 
            "Fiction", 
            "A classic novel of the Jazz Age", 
            "An American author and poet"
        );

        assertEquals("googleBookId123", book.getGoogleBookId());
        assertEquals("The Great Gatsby", book.getTitle());
        assertEquals("F. Scott Fitzgerald", book.getAuthor());
        assertEquals("Scribner", book.getPublisher());
        assertEquals("http://example.com/thumbnail.jpg", book.getThumbnailUrl());
        assertEquals("http://example.com/bigger-thumbnail.jpg", book.getBiggerThumbnailUrl());
        assertEquals(4.7, book.getAverageRating());
        assertEquals("1925-04-10", book.getPublishedDate());
        assertEquals("9780743273565", book.getIsbn());
        assertEquals("Fiction", book.getCategory());
        assertEquals("A classic novel of the Jazz Age", book.getDescription());
        assertEquals("An American author and poet", book.getAuthorDescription());
    }

    @Test
    public void testConstructorWithoutOptionalFields() {
        Book book = new Book(
            "googleBookId456", 
            "To Kill a Mockingbird", 
            "Harper Lee", 
            "J.B. Lippincott & Co.", 
            null, 
            null, 
            4.8, 
            "1960-07-11", 
            "9780060935467", 
            "Fiction", 
            null, 
            null
        );

        assertEquals("googleBookId456", book.getGoogleBookId());
        assertEquals("To Kill a Mockingbird", book.getTitle());
        assertEquals("Harper Lee", book.getAuthor());
        assertEquals("J.B. Lippincott & Co.", book.getPublisher());
        assertEquals(null, book.getThumbnailUrl());
        assertEquals(null, book.getBiggerThumbnailUrl());
        assertEquals(4.8, book.getAverageRating());
        assertEquals("1960-07-11", book.getPublishedDate());
        assertEquals("9780060935467", book.getIsbn());
        assertEquals("Fiction", book.getCategory());
        assertEquals(null, book.getDescription());
        assertEquals(null, book.getAuthorDescription());
    }

    @Test
    public void testSettersAndGetters() {
        Book book = new Book();
        
        book.setGoogleBookId("googleBookId789");
        assertEquals("googleBookId789", book.getGoogleBookId());
        
        book.setTitle("Brave New World");
        assertEquals("Brave New World", book.getTitle());
        
        book.setAuthor("Aldous Huxley");
        assertEquals("Aldous Huxley", book.getAuthor());
        
        book.setPublisher("Chatto & Windus");
        assertEquals("Chatto & Windus", book.getPublisher());
        
        book.setThumbnailUrl("http://example.com/thumbnail-new.jpg");
        assertEquals("http://example.com/thumbnail-new.jpg", book.getThumbnailUrl());
        
        book.setBiggerThumbnailUrl("http://example.com/bigger-thumbnail-new.jpg");
        assertEquals("http://example.com/bigger-thumbnail-new.jpg", book.getBiggerThumbnailUrl());
        
        book.setAverageRating(4.5);
        assertEquals(4.5, book.getAverageRating());
        
        book.setPublishedDate("1932-08-30");
        assertEquals("1932-08-30", book.getPublishedDate());
        
        book.setIsbn("9780060850524");
        assertEquals("9780060850524", book.getIsbn());
        
        book.setCategory("Dystopian");
        assertEquals("Dystopian", book.getCategory());
        
        book.setDescription("A novel about a dystopian future.");
        assertEquals("A novel about a dystopian future.", book.getDescription());
        
        book.setAuthorDescription("An English writer and philosopher.");
        assertEquals("An English writer and philosopher.", book.getAuthorDescription());
    }
}
