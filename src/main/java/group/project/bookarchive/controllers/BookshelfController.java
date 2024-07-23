package group.project.bookarchive.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import group.project.bookarchive.models.Bookshelf;
import group.project.bookarchive.models.BookshelfItem;
import group.project.bookarchive.models.BookshelfItemDTO;
import group.project.bookarchive.services.BookshelfService;

@RestController
@RequestMapping("/bookshelves")
public class BookshelfController {
    @Autowired
    private BookshelfService bookshelfService;

    @PostMapping("/create")
    public ResponseEntity<Bookshelf> createBookshelf(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        boolean isSecret = Boolean.parseBoolean(request.get("secret"));
        Bookshelf bookshelf = bookshelfService.createBookshelf(name, isSecret);
        return ResponseEntity.ok(bookshelf);
    }

    @PostMapping("/rename")
    public ResponseEntity<Bookshelf> renameBookshelf(@RequestBody Map<String, String> request) {
        String oldName = request.get("oldName");
        String newName = request.get("newName");
        Bookshelf bookshelf = bookshelfService.renameBookshelf(oldName, newName);
        return ResponseEntity.ok(bookshelf);
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteBookshelf(@RequestBody Map<String, String> request) {
        Long id = Long.parseLong(request.get("id"));
        bookshelfService.deleteBookshelf(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/sort")
    public ResponseEntity<List<Bookshelf>> sortBookshelves(@RequestParam String sortBy) {
        List<Bookshelf> sortedBookshelves = bookshelfService.sortBookshelves(sortBy);
        return ResponseEntity.ok(sortedBookshelves);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Bookshelf>> getAllBookshelves() {
        List<Bookshelf> bookshelves = bookshelfService.getAllBookshelvesByUser();
        return ResponseEntity.ok(bookshelves);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookshelfItemDTO> getBookshelfDetails(@PathVariable Long id) {
        Optional<Bookshelf> bookshelfOptional = bookshelfService.getBookshelfById(id);
        if (bookshelfOptional.isPresent()) {
            Bookshelf bookshelf = bookshelfOptional.get();
            List<BookshelfItem> items = bookshelfService.getBookshelfItems(id);
            return ResponseEntity.ok(new BookshelfItemDTO(bookshelf, items));
        }
        return ResponseEntity.notFound().build();
    }

}
