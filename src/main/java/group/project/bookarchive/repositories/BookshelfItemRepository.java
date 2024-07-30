package group.project.bookarchive.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import group.project.bookarchive.models.Book;
import group.project.bookarchive.models.Bookshelf;
import group.project.bookarchive.models.BookshelfItem;

public interface BookshelfItemRepository extends JpaRepository<BookshelfItem, Long> {
    List<BookshelfItem> findByBookshelfId(Long bookshelfId);

    List<BookshelfItem> findByBook(Book book);

    BookshelfItem findByBookshelfIdAndBookId(Long bookshelfId, Long bookId);

    BookshelfItem findByBookshelfAndBook(Bookshelf bookshelf, Book book);

    boolean existsByBookshelfAndBook(Bookshelf bookshelf, Book book);
}
