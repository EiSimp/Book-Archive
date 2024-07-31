package group.project.bookarchive.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import group.project.bookarchive.models.Bookshelf;
import group.project.bookarchive.models.BookshelfDTO;
import group.project.bookarchive.models.BookshelfItem;
import group.project.bookarchive.models.BookshelfItemDTO;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.BookshelfItemRepository;
import group.project.bookarchive.repositories.BookshelfRepository;
import group.project.bookarchive.repositories.UserRepository;

@Service
public class BookshelfService {
    @Autowired
    private BookshelfRepository bookshelfRepository;

    @Autowired
    private BookshelfItemRepository bookshelfItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookshelfItemService bookshelfItemService;

    public Bookshelf createBookshelf(String name, boolean isSecret) {
        User user = getCurrentUser();
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setName(name);
        bookshelf.setSecret(isSecret);
        bookshelf.setUserId(user.getId());
        bookshelf.setDefault(false);
        return bookshelfRepository.save(bookshelf);
    }

    public Bookshelf renameBookshelf(String oldName, String newName) {
        Bookshelf bookshelf = bookshelfRepository.findByName(oldName);
        if (bookshelf != null) {
            bookshelf.setName(newName);
            bookshelfRepository.save(bookshelf);
        }
        return bookshelf;
    }

    public void deleteBookshelf(Long id) {
        Optional<Bookshelf> bookshelfOptional = bookshelfRepository.findById(id);
        if (bookshelfOptional.isPresent()) {
            Bookshelf bookshelf = bookshelfOptional.get();
            List<BookshelfItem> items = bookshelfItemRepository.findByBookshelfId(bookshelf.getId());
            bookshelfItemRepository.deleteAll(items);

            // Now delete the bookshelf
            bookshelfRepository.delete(bookshelf);
        }
    }

    public List<Bookshelf> sortBookshelves(String sortBy) {
        List<Bookshelf> bookshelves = getAllBookshelvesByUser();
        if ("name".equals(sortBy)) {
            bookshelves.sort(Comparator.comparing(Bookshelf::getName));
        }
        return bookshelves;
    }

    public List<Bookshelf> getAllBookshelvesByUser() {
        User user = getCurrentUser();
        return bookshelfRepository.findByUserId(user.getId());
    }

    public List<Bookshelf> getAllBookshelvesByUserId(Long id) {
        return bookshelfRepository.findByUserId(id);
    }

    public Optional<Bookshelf> getBookshelfById(Long id) {
        return bookshelfRepository.findById(id);
    }

    public List<BookshelfItem> getBookshelfItems(Long bookshelfId) {
        return bookshelfItemRepository.findByBookshelfId(bookshelfId);
    }

    public List<BookshelfItemDTO> getBookshelfDetails(Long bookshelfId) {
        Optional<Bookshelf> optionalBookshelf = bookshelfRepository.findById(bookshelfId);
        if (optionalBookshelf.isPresent()) {
            Bookshelf bookshelf = optionalBookshelf.get();
            List<BookshelfItem> items = getBookshelfItems(bookshelfId);

            // Create a list of BookshelfItemDTO from the list of BookshelfItem
            List<BookshelfItemDTO> bookshelfItemDTOs = items.stream()
                    .map(item -> new BookshelfItemDTO(bookshelf, item.getBook(), item))
                    .collect(Collectors.toList());

            return bookshelfItemDTOs;
        } else {
            throw new RuntimeException("Bookshelf not found");
        }
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

    public List<BookshelfDTO> findBookshelvesContainingBook(String googleBookId, Long userId) {
        List<BookshelfItem> items = bookshelfItemService.findItemsByGoogleBookId(googleBookId);
        return items.stream()
                .map(BookshelfItem::getBookshelf)
                .distinct() // Ensure unique bookshelves
                .filter(bookshelf -> !bookshelf.isSecret() || bookshelf.getUserId().equals(userId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BookshelfDTO convertToDTO(Bookshelf bookshelf) {
        return new BookshelfDTO(bookshelf);
    }

    public void createDefaultBookshelvesForUser(User user) {
        // Set to private by default
        createDefaultBookshelf(user, "Read", true);
        createDefaultBookshelf(user, "Reading", true);
        createDefaultBookshelf(user, "To Read", true);
        createDefaultBookshelf(user, "Wishlist", true);
    }

    public Bookshelf createDefaultBookshelf(User user, String name, boolean isSecret) {
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setName(name);
        bookshelf.setSecret(isSecret);
        bookshelf.setUserId(user.getId());
        bookshelf.setDefault(true);
        return bookshelfRepository.save(bookshelf);
    }

    public boolean isBookInBookshelf(Long bookshelfId, Long bookId) {

        return bookshelfItemRepository.existsByBookshelfIdAndBookId(bookshelfId, bookId);
    }
}
