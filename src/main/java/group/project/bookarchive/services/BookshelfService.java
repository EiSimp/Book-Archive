package group.project.bookarchive.services;

import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import group.project.bookarchive.models.Bookshelf;
import group.project.bookarchive.repositories.BookshelfRepository;

@Service
public class BookshelfService {
    @Autowired
    private BookshelfRepository bookshelfRepository;

    public Bookshelf createBookshelf(String name, boolean isSecret) {
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setName(name);
        bookshelf.setSecret(isSecret);
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

    public void deleteBookshelf(String name) {
        Bookshelf bookshelf = bookshelfRepository.findByName(name);
        if (bookshelf != null) {
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

    public List<Bookshelf> getAllBookshelves() {
        return bookshelfRepository.findAll();
    }

}
