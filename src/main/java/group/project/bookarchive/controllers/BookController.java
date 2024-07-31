package group.project.bookarchive.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import group.project.bookarchive.models.Book;
import group.project.bookarchive.repositories.BookshelfItemRepository;
import group.project.bookarchive.services.BookService;

@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookshelfItemRepository booksRepository;

    @GetMapping("/bookdetails/{googleBookId}")
    public ResponseEntity<Book> getBookDetails(@PathVariable String googleBookId) {
        try {
            Book book = bookService.fetchBookDetails(googleBookId);
            return ResponseEntity.ok(book);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
