package group.project.bookarchive.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import group.project.bookarchive.models.Book;
import group.project.bookarchive.models.BookComment;
import group.project.bookarchive.models.BookCommentWithRatingDTO;
import group.project.bookarchive.models.Bookshelf;
import group.project.bookarchive.models.BookshelfItem;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.BookCommentRepository;
import group.project.bookarchive.repositories.BookRepository;
import group.project.bookarchive.repositories.BookshelfItemRepository;
import group.project.bookarchive.repositories.BookshelfRepository;
import group.project.bookarchive.repositories.UserRepository;

@Service
public class BookCommentService {

    @Autowired
    private BookCommentRepository bookCommentRepository;

    @Autowired
    private BookshelfRepository bookshelfRepository;

    @Autowired
    private BookshelfItemRepository bookshelfItemRepository;

    @Autowired
    private UserRepository userRepository; // For fetching user details

    @Autowired
    private BookRepository bookRepository;

    public void addComment(String username, String googleBookId, String commentText) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            BookComment bookComment = new BookComment();
            bookComment.setUser(user);  // User object should be set
        bookComment.setGoogleBookId(googleBookId); // GoogleBookId should be set
        bookComment.setUserComment(commentText); // Comment text should be set
        bookComment.setCreatedDate(LocalDateTime.now()); // Initialize non-nullable field
        bookComment.setLastEditedDate(LocalDateTime.now());

            bookCommentRepository.save(bookComment);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    public void updateComment(Long commentId, String newCommentText) {
        Optional<BookComment> optionalComment = bookCommentRepository.findById(commentId);

        optionalComment.ifPresent(comment -> {
            // Handle the BookComment object here
            comment.setUserComment(newCommentText);
            bookCommentRepository.save(comment);
        });

        if (!optionalComment.isPresent()) {
            throw new NoSuchElementException("Comment not found with ID: " + commentId);
        }

    }

    public void deleteComment(Long commentId) {
        bookCommentRepository.deleteById(commentId);
    }

    // public List<BookComment> getCommentsForBook(String googleBookId) {
    //     return bookCommentRepository.findByGoogleBookId(googleBookId);
    // }

    public List<BookCommentWithRatingDTO> getCommentsWithRatings(String googleBookId) {
        List<BookComment> comments = bookCommentRepository.findByGoogleBookId(googleBookId);
        List<BookCommentWithRatingDTO> result = new ArrayList<>();
        
        for (BookComment comment : comments) {
            Long userId = comment.getUser().getId();

            // Fetch the Read bookshelf for the user
            Bookshelf readBookshelf = bookshelfRepository.findByUserIdAndName(userId, "Read")
                    .orElse(null);

            if (readBookshelf != null) {
                // Fetch the book entity based on the googleBookId
                Book book = bookRepository.findByGoogleBookId(googleBookId);

                if (book != null) {
                    // Find the BookshelfItem based on the bookshelf and book
                    BookshelfItem bookshelfItem = (BookshelfItem) bookshelfItemRepository.findByBookshelfAndBook(readBookshelf, book);

                    // Extract rating
                    Double rating = (bookshelfItem != null) ? bookshelfItem.getUserRating() : null;
                    result.add(new BookCommentWithRatingDTO(comment, rating));
                } else {
                    result.add(new BookCommentWithRatingDTO(comment, null)); // No book found
                }
            } else {
                result.add(new BookCommentWithRatingDTO(comment, null)); // No Read bookshelf found
            }
        }

        return result;
    }
}
