package group.project.bookarchive.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group.project.bookarchive.models.BookClub;
import group.project.bookarchive.models.BookClubMember;
import group.project.bookarchive.models.Bookshelf;
import group.project.bookarchive.models.BookshelfItem;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.BookClubMemberRepository;
import group.project.bookarchive.repositories.BookClubRepository;
import group.project.bookarchive.repositories.BookshelfItemRepository;
import group.project.bookarchive.repositories.BookshelfRepository;
import group.project.bookarchive.repositories.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class BookClubService {

    @Autowired
    private BookClubRepository bookClubRepository;

    @Autowired
    private BookshelfRepository bookshelfRepository;

    @Autowired
    private BookshelfItemRepository bookshelfItemRepository;

    @Autowired
    private BookClubMemberRepository bookClubMemberRepository;

    @Autowired
    private UserRepository userRepository;

    public BookClub createBookClub(String name, String description) {
        User manager = getCurrentUser();
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setName(name + " Bookshelf");
        bookshelf.setUserId(manager.getId());
        bookshelf = bookshelfRepository.save(bookshelf);

        BookClub bookClub = new BookClub();
        bookClub.setName(name);
        bookClub.setDescription(description);
        bookClub.setDateCreated(LocalDateTime.now());
        bookClub.setManagerUserId(manager.getId());
        bookClub.setBookshelfId(bookshelf.getId());

        return bookClubRepository.save(bookClub);
    }

    public BookClub renameBookClub(Long id, String newName, Long userId) {
        BookClub bookClub = getBookClubByIdAndManager(id, userId);
        bookClub.setName(newName);
        return bookClubRepository.save(bookClub);
    }

    public void deleteBookClub(Long id, Long userId) {
        BookClub bookClub = getBookClubByIdAndManager(id, userId);
        List<BookClubMember> members = bookClubMemberRepository.findByBookClubId(bookClub.getId());
        bookClubMemberRepository.deleteAll(members);
        List<BookshelfItem> items = bookshelfItemRepository.findByBookshelfId(bookClub.getBookshelfId());
        bookshelfItemRepository.deleteAll(items);
        bookshelfRepository.deleteById(bookClub.getBookshelfId());
        bookClubRepository.delete(bookClub);
    }

    public void transferManager(Long bookClubId, Long newManagerUserId, Long currentManagerUserId) {
        BookClub bookClub = getBookClubByIdAndManager(bookClubId, currentManagerUserId);
        bookClub.setManagerUserId(newManagerUserId);
        bookClubRepository.save(bookClub);

        Bookshelf bookshelf = bookshelfRepository.findById(bookClub.getBookshelfId())
                .orElseThrow(() -> new RuntimeException("Bookshelf not found"));
        bookshelf.setUserId(newManagerUserId);
        bookshelfRepository.save(bookshelf);
    }

    public List<BookClub> getAllBookClubs() {
        return bookClubRepository.findAll();
    }

    public BookClub getBookClubDetails(Long bookClubId) {
        return bookClubRepository.findById(bookClubId)
                .orElseThrow(() -> new RuntimeException("Book Club not found"));
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private BookClub getBookClubByIdAndManager(Long id, Long userId) {
        return bookClubRepository.findById(id)
                .filter(bookClub -> bookClub.getManagerUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Book Club not found or user is not the manager"));
    }
}
