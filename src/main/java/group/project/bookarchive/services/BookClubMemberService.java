package group.project.bookarchive.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group.project.bookarchive.models.BookClub;
import group.project.bookarchive.models.BookClubMember;
import group.project.bookarchive.repositories.BookClubMemberRepository;
import group.project.bookarchive.repositories.BookClubRepository;

@Service
public class BookClubMemberService {

    @Autowired
    private BookClubMemberRepository bookClubMemberRepository;

    @Autowired
    private BookClubRepository bookClubRepository;

    public BookClubMember addMember(Long bookClubId, Long userId) {
        if (isUserInBookClub(bookClubId, userId)) {
            throw new RuntimeException("User is already a member of this book club");
        }

        BookClubMember member = new BookClubMember();
        member.setBookClubId(bookClubId);
        member.setUserId(userId);
        member.setJoinDate(LocalDateTime.now());

        return bookClubMemberRepository.save(member);
    }

    public void removeMember(Long bookClubId, Long userId, Long managerUserId) {
        BookClub bookClub = bookClubRepository.findById(bookClubId)
                .orElseThrow(() -> new RuntimeException("Book Club not found"));
        if (!bookClub.getManagerUserId().equals(managerUserId)) {
            throw new RuntimeException("Only the manager can remove members");
        }
        bookClubMemberRepository.deleteByBookClubIdAndUserId(bookClubId, userId);
    }

    public void leaveBookClub(Long bookClubId, Long userId) {
        bookClubMemberRepository.deleteByBookClubIdAndUserId(bookClubId, userId);
    }

    public List<BookClubMember> getMembers(Long bookClubId) {
        return bookClubMemberRepository.findByBookClubId(bookClubId);
    }

    public List<BookClubMember> getUserBookClubs(Long userId) {
        return bookClubMemberRepository.findByUserId(userId);
    }

    public boolean isUserInBookClub(Long bookClubId, Long userId) {
        return bookClubMemberRepository.findByBookClubIdAndUserId(bookClubId, userId) != null;
    }
}
