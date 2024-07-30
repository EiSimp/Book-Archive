package group.project.bookarchive.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import group.project.bookarchive.models.Bookshelf;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {
    Bookshelf findByName(String name);

    List<Bookshelf> findByUserId(Long userId);

    Optional<Bookshelf> findById(Long id);

    Bookshelf findByNameAndUserId(String name, Long userId);

    Optional<Bookshelf> findByUserIdAndName(Long userId, String string);

}
