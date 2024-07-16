package group.project.bookarchive.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import group.project.bookarchive.models.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByGoogleBookId(String googleBookId);
}
