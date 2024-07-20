package group.project.bookarchive.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import group.project.bookarchive.models.Bookshelf;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {
    Bookshelf findByName(String name);

    List<Bookshelf> findByUserId(Long userId);

    Bookshelf findByNameAndUserId(String name, Long userId);

}
