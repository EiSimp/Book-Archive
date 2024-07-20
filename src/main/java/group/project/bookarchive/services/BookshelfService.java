package group.project.bookarchive.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import group.project.bookarchive.models.Bookshelf;
import group.project.bookarchive.repositories.BookshelfRepository;
//Newly added imports
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.UserRepository;

@Service
public class BookshelfService {
    @Autowired
    private BookshelfRepository bookshelfRepository;

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
        Optional<Bookshelf> bookshelf = bookshelfRepository.findById(id);
        if (bookshelf != null) {
            bookshelfRepository.delete(bookshelf.get());
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
}
