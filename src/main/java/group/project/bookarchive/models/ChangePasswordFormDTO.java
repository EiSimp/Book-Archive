package group.project.bookarchive.models;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ChangePasswordFormDTO {
    @NotBlank(message = "Current password required")
    private String currentPassword;

    @NotBlank(message = "New password required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z]).{8,}$", message = "Password must contain at least 1 uppercase letter and 1 digit, and be at least 8 characters")
    private String newPassword;

    @NotBlank(message = "Confirm password required")
    private String confirmPassword;

    @AssertTrue(message = "Passwords don't match")
    private boolean passwordsMatch() {
        return newPassword.equals(confirmPassword);
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String oldPassword) {
        this.currentPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword1) {
        this.newPassword = newPassword1;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String newPassword2) {
        this.confirmPassword = newPassword2;
    }
}
