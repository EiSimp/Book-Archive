package group.project.bookarchive.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import group.project.bookarchive.models.Book;
import group.project.bookarchive.models.Bookshelf;
import group.project.bookarchive.models.BookshelfItem;
import group.project.bookarchive.repositories.BookRepository;
import group.project.bookarchive.repositories.BookshelfItemRepository;
import group.project.bookarchive.repositories.BookshelfRepository;

@Service
public class BookshelfItemService {
    @Autowired
    private BookshelfRepository bookshelfRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookshelfItemRepository bookshelfItemRepository;

    public BookshelfItem addBookToBookshelf(Long bookshelfId, Book book) {
        Bookshelf bookshelf = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new RuntimeException("Bookshelf not found"));

        Book existingBook = bookRepository.findByGoogleBookId(book.getGoogleBookId());
        if (existingBook == null) {
            existingBook = bookRepository.save(book);
        }

        BookshelfItem bookshelfItem = new BookshelfItem();
        bookshelfItem.setBook(existingBook);
        bookshelfItem.setBookshelf(bookshelf);

        return bookshelfItemRepository.save(bookshelfItem);
    }
}
