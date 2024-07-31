package group.project.bookarchive.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group.project.bookarchive.exceptions.BookAlreadyExistsException;
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

        // Check if the book is already in the bookshelf
        if (bookshelfItemRepository.existsByBookshelfAndBook(bookshelf, existingBook)) {
            throw new BookAlreadyExistsException("Book already exists in the bookshelf");
        }

        BookshelfItem bookshelfItem = new BookshelfItem();
        bookshelfItem.setBook(existingBook);
        bookshelfItem.setBookshelf(bookshelf);

        return bookshelfItemRepository.save(bookshelfItem);
    }

    public List<BookshelfItem> findItemsByGoogleBookId(String googleBookID) {
        Book book = bookRepository.findByGoogleBookId(googleBookID);
        if (book == null) {
            throw new RuntimeException("Book not found");
        }
        return bookshelfItemRepository.findByBook(book);
    }

    public void deleteBookFromBookshelf(Long bookshelfId, String googleBookId) {
        Bookshelf bookshelf = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new RuntimeException("Bookshelf not found"));

        Book book = bookRepository.findByGoogleBookId(googleBookId);
        if (book == null) {
            throw new RuntimeException("Book not found");
        }

        BookshelfItem bookshelfItem = bookshelfItemRepository.findByBookshelfAndBook(bookshelf, book);
        if (bookshelfItem != null) {
            bookshelfItemRepository.delete(bookshelfItem);
        } else {
            throw new RuntimeException("BookshelfItem not found");
        }
    }

    public BookshelfItem updateBookRating(Long bookshelfId, Book book, double rating) {
        Bookshelf bookshelf = bookshelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new RuntimeException("Bookshelf not found"));

        Book existingBook = bookRepository.findByGoogleBookId(book.getGoogleBookId());
        if (existingBook == null) {
            existingBook = bookRepository.save(book);
        }

        BookshelfItem bookshelfItem = (BookshelfItem) bookshelfItemRepository.findByBookshelfAndBook(bookshelf, existingBook);

        bookshelfItem.setUserRating(rating);

        return bookshelfItemRepository.save(bookshelfItem);
    }

    


}
