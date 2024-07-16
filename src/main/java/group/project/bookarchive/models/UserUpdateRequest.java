package group.project.bookarchive.models;

import java.util.Set;

import jakarta.validation.constraints.Pattern;

public class UserUpdateRequest {
    @Pattern(regexp = "^\\w+$", message = "Username must have only letters, numbers, and underscores")
    private String username;
    private String password;
    private String bio;
    private String email;
    private Set<String> roles;

    // Constructors
    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String username, String password, String bio) {
        this.username = username;
        this.password = password;
        this.bio = bio;
    }

    public UserUpdateRequest(String username, String password, String email, String bio) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.bio = bio;
    }

    public UserUpdateRequest(String username, String password, String bio, String email, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.email = email;
        this.roles = roles;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
