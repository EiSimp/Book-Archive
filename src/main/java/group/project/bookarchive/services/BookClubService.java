package group.project.bookarchive.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group.project.bookarchive.models.BookClub;
import group.project.bookarchive.models.BookClubDTO;
import group.project.bookarchive.models.BookClubMember;
import group.project.bookarchive.models.Bookshelf;
import group.project.bookarchive.models.BookshelfItem;
import group.project.bookarchive.models.Event;
import group.project.bookarchive.models.Messages;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.BookClubMemberRepository;
import group.project.bookarchive.repositories.BookClubRepository;
import group.project.bookarchive.repositories.BookshelfItemRepository;
import group.project.bookarchive.repositories.BookshelfRepository;
import group.project.bookarchive.repositories.EventRepository;
import group.project.bookarchive.repositories.MessageRepository;
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
    private MessageRepository messageRepository;

    @Autowired
    private EventRepository eventRepository;

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
        bookClub.setManager(manager);
        bookClub.setBookshelf(bookshelf);

        BookClub savedBookClub = bookClubRepository.save(bookClub);

        BookClubMember managerMember = new BookClubMember();
        managerMember.setBookClub(savedBookClub);
        managerMember.setUser(manager);
        managerMember.setJoinDate(LocalDateTime.now());
        bookClubMemberRepository.save(managerMember);

        return savedBookClub;
    }

    public BookClub renameBookClub(Long id, String newName, Long userId) {
        BookClub bookClub = getBookClubByIdAndManager(id, userId);
        bookClub.setName(newName);
        return bookClubRepository.save(bookClub);
    }

    @Transactional
    public void deleteBookClub(Long id, Long userId) {
        BookClub bookClub = getBookClubByIdAndManager(id, userId);
        List<BookClubMember> members = bookClubMemberRepository.findByBookClubId(bookClub.getId());
        bookClubMemberRepository.deleteAll(members);
        Bookshelf bookshelf = bookClub.getBookshelf();
        List<BookshelfItem> items = bookshelfItemRepository.findByBookshelfId(bookshelf.getId());
        bookshelfItemRepository.deleteAll(items);
        bookshelfRepository.deleteById(bookshelf.getId());
        List<Messages> messages = messageRepository.findByBookClubId(bookClub.getId());
        messageRepository.deleteAll(messages);
        List<Event> events = eventRepository.findByBookClubId(bookClub.getId());
        eventRepository.deleteAll(events);
        bookClubRepository.delete(bookClub);
    }

    public void transferManager(Long bookClubId, Long newManagerUserId, Long currentManagerUserId) {
        BookClub bookClub = getBookClubByIdAndManager(bookClubId, currentManagerUserId);
        User newManager = userRepository.findById(newManagerUserId)
                .orElseThrow(() -> new RuntimeException("New manager not found"));

        // Check if the new manager is already a member of the book club
        boolean isMember = bookClubMemberRepository.findByBookClubIdAndUserId(bookClubId, newManagerUserId) != null;
        if (!isMember) {
            // If the new manager is not a member, add them to the book club
            BookClubMember newMember = new BookClubMember();
            newMember.setBookClub(bookClub);
            newMember.setUser(newManager);
            newMember.setJoinDate(LocalDateTime.now());
            bookClubMemberRepository.save(newMember);
        }

        bookClub.setManager(newManager);
        bookClubRepository.save(bookClub);

        Bookshelf bookshelf = bookshelfRepository.findById(bookClub.getBookshelf().getId())
                .orElseThrow(() -> new RuntimeException("Bookshelf not found"));
        bookshelf.setUserId(newManagerUserId);
        bookshelfRepository.save(bookshelf);
    }

    public List<BookClub> getAllBookClubs() {
        return bookClubRepository.findAll();
    }

    public BookClubDTO convertToDTO(BookClub bookClub) {
        BookClubDTO dto = new BookClubDTO();
        dto.setId(bookClub.getId());
        dto.setName(bookClub.getName());
        dto.setDescription(bookClub.getDescription());
        dto.setManagerId(bookClub.getManager().getId());
        dto.setManagerName(bookClub.getManager().getUsername()); // Assuming User has a getUsername() method
        return dto;
    }

    public List<BookClubDTO> convertToDTOs(List<BookClub> bookClubs) {
        return bookClubs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
                .filter(bookClub -> bookClub.getManager().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Book Club not found or user is not the manager"));
    }

    public BookClub findBookClubById(Long id) {
        return bookClubRepository.findById(id).orElse(null);
    }
}