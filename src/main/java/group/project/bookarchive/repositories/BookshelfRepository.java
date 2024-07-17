package group.project.bookarchive.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import group.project.bookarchive.models.Bookshelf;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {
    Bookshelf findByName(String name);
    List<Bookshelf> findByUserId(Long userId);
}
