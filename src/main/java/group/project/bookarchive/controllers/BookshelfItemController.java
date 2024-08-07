package group.project.bookarchive.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import group.project.bookarchive.models.Book;
import group.project.bookarchive.models.Bookshelf;
import group.project.bookarchive.models.BookshelfItem;
import group.project.bookarchive.repositories.BookRepository;
import group.project.bookarchive.repositories.BookshelfItemRepository;
import group.project.bookarchive.repositories.BookshelfRepository;
import group.project.bookarchive.services.BookService;
import group.project.bookarchive.services.BookshelfItemService;

@RestController
@RequestMapping("/bookshelf-items")
public class BookshelfItemController {

    @Autowired
    private BookshelfItemService bookshelfItemService;

    @Autowired
    private BookshelfRepository bookshelfRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookshelfItemRepository bookshelfItemRepository;

    @PostMapping("/add")
    public ResponseEntity<BookshelfItem> addBookToBookshelf(@RequestParam Long bookshelfId, @RequestBody Book book) {
        System.out.println("Received bookshelfId: " + bookshelfId);
        BookshelfItem bookshelfItem = bookshelfItemService.addBookToBookshelf(bookshelfId, book);
        Long id = book.getId();
        // Book existingBook = bookRepository.findById(id);
        bookService.updateBookAverageRating(id);
        return ResponseEntity.ok(bookshelfItem);
    }

    @DeleteMapping("/{bookshelfId}/{bookId}")
    public ResponseEntity<Void> deleteBookFromBookshelf(@PathVariable Long bookshelfId, @PathVariable String bookId) {
        bookshelfItemService.deleteBookFromBookshelf(bookshelfId, bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> isBookInBookshelf(
            @RequestParam Long bookshelfId,
            @RequestParam String googleBookId) {

        try {
            Bookshelf bookshelf = bookshelfRepository.findById(bookshelfId)
                    .orElseThrow(() -> new RuntimeException("Bookshelf not found"));

            Book existingBook = bookRepository.findByGoogleBookId(googleBookId);

            boolean isBookPresent = bookshelfItemRepository.existsByBookshelfAndBook(bookshelf, existingBook);
            return ResponseEntity.ok(isBookPresent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/rating")
    public ResponseEntity<BookshelfItem> updateBookRating(
            @RequestParam Long bookshelfId,
            @RequestParam String googleBookId,
            @RequestParam double rating) {

        try {
            Book existingBook = bookRepository.findByGoogleBookId(googleBookId);

            BookshelfItem updatedItem = bookshelfItemService.updateBookRating(bookshelfId, existingBook, rating);
            
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rating")
    public ResponseEntity<Double> getBookRating(
            @RequestParam Long bookshelfId,
            @RequestParam String googleBookId) {

        try {
            Book existingBook = bookRepository.findByGoogleBookId(googleBookId);
            if (existingBook == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Bookshelf bookshelf = bookshelfRepository.findById(bookshelfId)
                    .orElseThrow(() -> new RuntimeException("Bookshelf not found"));

            BookshelfItem bookshelfItem = (BookshelfItem) bookshelfItemRepository.findByBookshelfAndBook(bookshelf, existingBook);
            if (bookshelfItem == null) {
                return ResponseEntity.ok(0.0); // Return 0.0 if no rating is found

            }

            bookService.updateBookAverageRating(existingBook.getId());

            double rating = bookshelfItem.getUserRating();
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/updateComment")
    public ResponseEntity<?> updateComment(@RequestParam Long bookshelfItemId, 
                                            @RequestParam String comment) {
        // Find the bookshelf item by its ID
        return bookshelfItemRepository.findById(bookshelfItemId)
            .map(item -> {
                // Update the comment
                item.setUserComment(comment);
                bookshelfItemRepository.save(item);
                return ResponseEntity.ok().build();
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found"));
    }

    @GetMapping("/books")
    public Page<BookshelfItem> getBooksByBookshelf(
        @RequestParam Long bookshelfId,
        @RequestParam int page,
        @RequestParam int size) {

        Pageable pageable = PageRequest.of(page, size);
        return bookshelfItemRepository.findByBookshelfId(bookshelfId, pageable);
    }
}
