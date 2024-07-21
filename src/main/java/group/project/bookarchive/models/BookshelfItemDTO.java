package group.project.bookarchive.models;

import java.util.List;

public class BookshelfItemDTO {
    private Bookshelf bookshelf;
    private List<BookshelfItem> items;

    public BookshelfItemDTO(Bookshelf bookshelf, List<BookshelfItem> items) {
        this.bookshelf = bookshelf;
        this.items = items;
    }

    public Bookshelf getBookshelf() {
        return bookshelf;
    }

    public List<BookshelfItem> getItems() {
        return items;
    }
}
