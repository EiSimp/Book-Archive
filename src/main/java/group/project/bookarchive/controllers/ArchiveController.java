package group.project.bookarchive.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestParam;

import group.project.bookarchive.models.SignupFormDTO;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.UserRepository;
import group.project.bookarchive.security.SecurityUser;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ArchiveController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/archives/view")
    public String getAllUsers() {
        return "homepage";
    }

    @GetMapping("/homepage")
    public String homepage() {
        return "homepage";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("signupform", new SignupFormDTO());
        return "signup";
    }

    @GetMapping("/login")
    public String getLogin(Model model, @AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            return "login";
        } else {
            return "homepage";
        }
    }

    @GetMapping("/forgot")
    public String forgotpwd() {
        return "forgotpwd";
    }

    @GetMapping("/bookclubs")
    public String bookclubs() {
        return "bookclubs";
    }

    @GetMapping("/myrecord")
    public String myrecord() {
        return "myrecord";
    }

    @GetMapping("/profilesetting")
    public String profileSetting() {
        return "profilesetting";
    }

    @PostMapping("/forgot")
    public String forgotPassword(@RequestBody String entity) {
        // TODO: process POST request

        return entity;
    }

    //
    @PostMapping("/signup")
    public String addUser(@Valid @ModelAttribute("signupform") SignupFormDTO form,
            BindingResult result,
            Model model) {

        if (userRepository.existsByUsername(form.getUsername())) {
            result.rejectValue("username", "",
                    "There is already an account registered with the same username");
        }

        if (result.hasErrors()) {
            model.addAttribute("signupform", form);
            return "/signup";
        }

        userRepository.save(new User(form.getUsername(), new BCryptPasswordEncoder().encode(form.getPassword())));
        return "redirect:/login?signupsuccess";
    }

    // mapping for change password page
    @GetMapping("/passwordchange")
    public String changePassword() {
        return "passwordchange";
    }

    // post for change password
    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal SecurityUser user,
            @RequestParam("current-password") String currentPassword,
            @RequestParam("new-password") String newPassword,
            @RequestParam("confirm-password") String confirmPassword,
            Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New password and confirmation do not match");
            return "passwordchange";
        }

        User currentUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            model.addAttribute("error", "Current password is incorrect");
            return "passwordchange";
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);

        return "redirect:/homepage?passwordchangesuccess";
    }
}
