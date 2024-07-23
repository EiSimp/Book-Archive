package group.project.bookarchive.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookclubmembers", uniqueConstraints = @UniqueConstraint(columnNames = { "book_club_id", "user_id" }))
public class BookClubMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "join_date")
    private LocalDateTime joinDate;

    @Column(name = "book_club_id", insertable = false, updatable = false)
    private Long bookClubId;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "book_club_id")
    private BookClub bookClub;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Long getBookClubId() {
        return bookClubId;
    }

    public void setBookClubId(Long bookClubId) {
        this.bookClubId = bookClubId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BookClub getBookClub() {
        return bookClub;
    }

    public void setBookClub(BookClub bookClub) {
        this.bookClub = bookClub;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
