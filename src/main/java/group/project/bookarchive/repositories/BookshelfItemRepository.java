package group.project.bookarchive.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import group.project.bookarchive.models.Book;
import group.project.bookarchive.models.Bookshelf;
import group.project.bookarchive.models.BookshelfItem;

public interface BookshelfItemRepository extends JpaRepository<BookshelfItem, Long> {
    List<BookshelfItem> findByBookshelfId(Long bookshelfId);

    boolean existsByBookshelfAndBook(Bookshelf bookshelf, Book book);
    boolean existsByBookshelfIdAndBookId(Long bookshelfId, Long bookId);

    public Object findByBookshelfAndBook(Bookshelf bookshelf, Book existingBook);
}
