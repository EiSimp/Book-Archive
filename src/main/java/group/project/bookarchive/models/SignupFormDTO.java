package group.project.bookarchive.models;

import jakarta.validation.constraints.Pattern;

public class SignupFormDTO {
    @Pattern(regexp = "^\\w+$", message = "Username must have only letters, numbers, and underscores")
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z]).{8,}$", message = "Password must contain at least 1 uppercase letter and 1 digit, and be at least 8 characters")
    private String password;

    @Pattern(regexp = "^[A-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[A-Z0-9.-]+$", message = "Email address must be valid")
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
