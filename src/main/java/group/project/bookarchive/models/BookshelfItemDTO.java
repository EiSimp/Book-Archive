package group.project.bookarchive.models;

import java.util.List;
import java.util.stream.Collectors;

public class BookshelfItemDTO {
    private BookshelfDTO bookshelf;
    private List<BookDTO> items;

    public BookshelfItemDTO(Bookshelf bookshelf, List<BookshelfItem> items) {
        this.bookshelf = new BookshelfDTO(bookshelf);
        this.items = items.stream().map(item -> new BookDTO(item.getBook())).collect(Collectors.toList());

    }

    public BookshelfDTO getBookshelf() {
        return bookshelf;
    }

    public List<BookDTO> getItems() {
        return items;
    }
}
