package group.project.bookarchive.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public void removeOrLeaveBookClub(Long bookClubId, Long userId, Long managerUserId) {
        BookClub bookClub = bookClubRepository.findById(bookClubId)
                .orElseThrow(() -> new RuntimeException("Book Club not found"));

        if (bookClub.getManagerUserId().equals(userId)) {
            throw new RuntimeException(
                    "Manager cannot remove themselves or leave the book club. Transfer manager role before removing.");
        }
        if (!bookClub.getManagerUserId().equals(managerUserId) && !userId.equals(managerUserId)) {
            throw new RuntimeException("Only the manager can remove members or a user can leave the club themselves");
        }

        bookClubMemberRepository.deleteByBookClubIdAndUserId(bookClubId, userId);
    }

    public List<BookClubMember> getMembers(Long bookClubId) {
        return bookClubMemberRepository.findByBookClubId(bookClubId);
    }

    public List<BookClub> getUserBookClubs(Long userId) {
        List<BookClubMember> bookClubMembers = bookClubMemberRepository.findByUserId(userId);
        List<Long> bookClubIds = bookClubMembers.stream()
                .map(BookClubMember::getBookClubId)
                .collect(Collectors.toList());
        return bookClubRepository.findAllById(bookClubIds);
    }

    public boolean isUserInBookClub(Long bookClubId, Long userId) {
        return bookClubMemberRepository.findByBookClubIdAndUserId(bookClubId, userId) != null;
    }
}
