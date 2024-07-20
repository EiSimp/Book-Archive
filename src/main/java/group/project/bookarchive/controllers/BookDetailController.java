package group.project.bookarchive.controllers;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import group.project.bookarchive.models.Book;
import group.project.bookarchive.services.BookService;
import group.project.bookarchive.services.BookshelfService;

@Controller
public class BookDetailController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookshelfService bookshelfService;

    @GetMapping("/bookdetail")
    public String getBookDetail(@RequestParam("id") String bookID, Model model) {
        try {
            Book book = bookService.fetchBookDetails(bookID);
            model.addAttribute("book", book);
            //Changed getAllBookshelves to getAllBookshelvesByUser
            model.addAttribute("bookshelves", bookshelfService.getAllBookshelvesByUser());
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Unable to fetch book details.");
        }
        return "bookdetail";
    }
}
