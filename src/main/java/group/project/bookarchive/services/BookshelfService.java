package group.project.bookarchive.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import group.project.bookarchive.models.Bookshelf;
import group.project.bookarchive.models.BookshelfItem;
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

    public Bookshelf createBookshelf(String name, boolean isSecret) {
        User user = getCurrentUser();
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setName(name);
        bookshelf.setSecret(isSecret);
        bookshelf.setUserId(user.getId());
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
        List<Bookshelf> bookshelves = bookshelfRepository.findAll();
        if ("name".equals(sortBy)) {
            bookshelves.sort(Comparator.comparing(Bookshelf::getName));
        }
        return bookshelves;
    }

    public List<Bookshelf> getAllBookshelvesByUser() {
        User user = getCurrentUser();
        return bookshelfRepository.findByUserId(user.getId());
    }

    public Optional<Bookshelf> getBookshelfById(Long id) {
        return bookshelfRepository.findById(id);
    }

    public List<BookshelfItem> getBookshelfItems(Long bookshelfId) {
        return bookshelfItemRepository.findByBookshelfId(bookshelfId);
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

    public void createDefaultBookshelvesForUser(User user) {
        // Set to private by default
        createDefaultBookshelf(user, "Books-Read", true);
        createDefaultBookshelf(user, "Books-Reading", true);
        createDefaultBookshelf(user, "Books-to-Read", true);
    }

    public Bookshelf createDefaultBookshelf(User user, String name, boolean isSecret) {
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setName(name);
        bookshelf.setSecret(isSecret);
        bookshelf.setUserId(user.getId());
        return bookshelfRepository.save(bookshelf);
    }
}
