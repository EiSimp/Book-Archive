package group.project.bookarchive.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import group.project.bookarchive.models.BookClub;

public interface BookClubRepository extends JpaRepository<BookClub, Long> {
    Optional<BookClub> findById(Long id);

    List<BookClub> findByManagerId(Long managerId);
}
