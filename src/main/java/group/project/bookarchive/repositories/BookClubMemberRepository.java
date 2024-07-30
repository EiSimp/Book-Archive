package group.project.bookarchive.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import group.project.bookarchive.models.BookClubMember;

public interface BookClubMemberRepository extends JpaRepository<BookClubMember, Long> {
    List<BookClubMember> findByBookClubId(Long bookClubId);

    List<BookClubMember> findByUserId(Long userId);

    BookClubMember findByBookClubIdAndUserId(Long bookClubId, Long userId);

    @Transactional
    void deleteByBookClubIdAndUserId(Long bookClubId, Long userId);
}
