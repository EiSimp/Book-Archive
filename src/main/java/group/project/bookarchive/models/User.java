package group.project.bookarchive.models;
//This is where we keep all user's data.

import jakarta.persistence.*;

//Specifies that the class is an entity mapped to a database
@Entity
// Specifies the name of the table in the database
@Table(name = "userinfo")
public class User {
    // Specifies the primary key of the entity
    @Id
    // Specifies that the generated key is generated automatically
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Specifies that the username has to be unique and cannot be empty
    @Column(unique = true, nullable = false)
    private String username;
    // Specifies that the password cannot be empty
    @Column(nullable = false)
    private String password;
    // Column for bio, added for testing
    @Column(length = 500)
    private String bio;

    // Default constructor
    public User() {
    }

    // Constructor with all parameters
    public User(String username, String password, String bio) {
        this.username = username;
        this.password = password;
        this.bio = bio;
    }

    // Constructor without bio
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters for ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getters and Setters for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getters and Setters for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getters and Setters for bio
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
