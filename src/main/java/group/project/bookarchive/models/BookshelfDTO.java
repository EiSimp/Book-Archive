package group.project.bookarchive.models;

public class BookshelfDTO {
    private Long id;
    private String name;
    private boolean isSecret;
    private Long userId;

    public BookshelfDTO() {
    }

    public BookshelfDTO(Bookshelf bookshelf) {
        this.id = bookshelf.getId();
        this.name = bookshelf.getName();
        this.isSecret = bookshelf.isSecret();
        this.userId = bookshelf.getUserId();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsSecret() {
        return this.isSecret;
    }

    public boolean getIsSecret() {
        return this.isSecret;
    }

    public void setIsSecret(boolean isSecret) {
        this.isSecret = isSecret;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
