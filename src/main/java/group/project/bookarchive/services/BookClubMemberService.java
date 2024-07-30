package group.project.bookarchive.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group.project.bookarchive.models.BookClub;
import group.project.bookarchive.models.BookClubMember;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.BookClubMemberRepository;
import group.project.bookarchive.repositories.BookClubRepository;
import group.project.bookarchive.repositories.UserRepository;

@Service
public class BookClubMemberService {

    @Autowired
    private BookClubMemberRepository bookClubMemberRepository;

    @Autowired
    private BookClubRepository bookClubRepository;

    @Autowired
    private UserRepository userRepository;

    public BookClubMember addMember(Long bookClubId, Long userId) {
        if (isUserInBookClub(bookClubId, userId)) {
            throw new RuntimeException("User is already a member of this book club");
        }

        List<BookClubMember> members = bookClubMemberRepository.findByBookClubId(bookClubId);
        if (members.size() >= 15) {
            throw new RuntimeException("Book club has reached the maximum number of members (15)");
        }

        BookClub bookClub = bookClubRepository.findById(bookClubId)
                .orElseThrow(() -> new RuntimeException("Book Club not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BookClubMember member = new BookClubMember();
        member.setBookClub(bookClub);
        member.setUser(user);
        member.setJoinDate(LocalDateTime.now());

        return bookClubMemberRepository.save(member);
    }

    @Transactional
    public void removeOrLeaveBookClub(Long bookClubId, Long userId, Long managerUserId) {
        BookClub bookClub = bookClubRepository.findById(bookClubId)
                .orElseThrow(() -> new RuntimeException("Book Club not found"));

        if (bookClub.getManager().getId().equals(userId)) {
            throw new RuntimeException(
                    "Manager cannot remove themselves or leave the book club. Transfer manager role before removing.");
        }
        if (!bookClub.getManager().getId().equals(managerUserId) && !userId.equals(managerUserId)) {
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
                .map(member -> member.getBookClub().getId())
                .collect(Collectors.toList());
        return bookClubRepository.findAllById(bookClubIds);
    }

    public boolean isUserInBookClub(Long bookClubId, Long userId) {
        return bookClubMemberRepository.findByBookClubIdAndUserId(bookClubId, userId) != null;
    }
}
