package group.project.bookarchive.repositories;

import group.project.bookarchive.models.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Messages, Long> {
    List<Messages> findByBookClubId(Long bookClubId);

    List<Messages> findByBookClubIdAndContentContaining(Long bookClubId, String content);
}
