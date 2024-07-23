package group.project.bookarchive.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookclub")
public class BookClub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "manager_user_id")
    private Long managerUserId;

    @Column(name = "bookshelf_id")
    private Long bookshelfId;

    @ManyToOne
    @JoinColumn(name = "manager_user_id")
    private User manager;

    @OneToOne
    @JoinColumn(name = "bookshelf_id")
    private Bookshelf bookshelf;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getManagerUserId() {
        return managerUserId;
    }

    public void setManagerUserId(Long managerUserId) {
        this.managerUserId = managerUserId;
    }

    public Long getBookshelfId() {
        return bookshelfId;
    }

    public void setBookshelfId(Long bookshelfId) {
        this.bookshelfId = bookshelfId;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public Bookshelf getBookshelf() {
        return bookshelf;
    }

    public void setBookshelf(Bookshelf bookshelf) {
        this.bookshelf = bookshelf;
    }
}
