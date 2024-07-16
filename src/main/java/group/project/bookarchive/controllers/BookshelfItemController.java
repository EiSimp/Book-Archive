package group.project.bookarchive.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import group.project.bookarchive.models.Book;
import group.project.bookarchive.models.BookshelfItem;
import group.project.bookarchive.services.BookshelfItemService;

@RestController
@RequestMapping("/bookshelf-items")
public class BookshelfItemController {
    @Autowired
    private BookshelfItemService bookshelfItemService;

    @PostMapping("/add")
    public ResponseEntity<BookshelfItem> addBookToBookshelf(@RequestParam Long bookshelfId, @RequestBody Book book) {
        System.out.println("Received bookshelfId: " + bookshelfId);
        BookshelfItem bookshelfItem = bookshelfItemService.addBookToBookshelf(bookshelfId, book);
        return ResponseEntity.ok(bookshelfItem);
    }
}
