package group.project.bookarchive.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import group.project.bookarchive.models.BookComment;

public interface BookCommentRepository extends JpaRepository<BookComment, Long> {
    List<BookComment> findByGoogleBookId(String googleBookId);

    List<BookComment> findByUserId(Long userId);
}