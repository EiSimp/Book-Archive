package group.project.bookarchive.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import group.project.bookarchive.models.MailDTO;
import group.project.bookarchive.models.SignupFormDTO;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.UserRepository;
import group.project.bookarchive.security.SecurityUser;
import group.project.bookarchive.services.MailService;
import group.project.bookarchive.services.UserService;
import jakarta.validation.Valid;

@Controller
public class ArchiveController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService service;

    @Autowired
    private MailService mailService;

    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("login");
    }

    @GetMapping("/admin/users")
    public String getAllUsers(Model model) {
        List<User> listUsers = service.listAll();
        model.addAttribute("listUsers", listUsers);
        return "users";
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

    @GetMapping("/header")
    public String getHeader() {
        return "fragments/header.html";
    }

    @GetMapping("/bookdetail")
    public String getBookDetail() {
        return "bookdetail";
    }

    @PostMapping("/forgot")
    public String forgotPwdMail(@ModelAttribute MailDTO mailDto, Model model) {
        if (mailDto.getEmail() == null || mailDto.getEmail().isEmpty()) {
            model.addAttribute("error", "Email cannot be empty");
            return "forgotpwd";
        }
        if (mailDto.getUsername() == null || mailDto.getUsername().isEmpty()) {
            model.addAttribute("error", "Username cannot be empty");
            return "forgotpwd";
        }

        Optional<User> user = userRepository.findByUsernameAndEmail(mailDto.getUsername(), mailDto.getEmail());
        if (user.isPresent()) {
            mailService.sendPwdMail(mailDto);
            System.out.println("sent email");
            return "redirect:/login";
        } else {
            model.addAttribute("error", "User not found");
            return "forgotpwd";
        }
    }

    @PostMapping("/signup")
    public String addUser(@Valid @ModelAttribute("signupform") SignupFormDTO form,
            BindingResult result,
            Model model) {

        if (userRepository.existsByUsername(form.getUsername())) {
            result.rejectValue("username", "",
                    "There is already an account registered with the same username");
        }

        if (userRepository.existsByEmail(form.getEmail())) {
            result.rejectValue("email", "", "There is already an account registered with the same email address");
        }

        if (result.hasErrors()) {
            model.addAttribute("signupform", form);
            return "/signup";
        }

        User user = new User(form.getUsername(), new BCryptPasswordEncoder().encode(form.getPassword()),
                form.getEmail());

        service.registerDefaultUser(userRepository.save(user));
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
        currentUser.setTempPwd(false);
        userRepository.save(currentUser);

        return "redirect:/homepage?passwordchangesuccess";
    }

    @GetMapping("/myaccount")
    public String showMyAccount(Model model, @AuthenticationPrincipal SecurityUser user) {
        // if (user == null) {
        // return "redirect:/login"; // Redirect to login page if user is not
        // authenticated.
        // } else {
        // Fetch updated user data from the database based on ID
        Optional<User> userOptional = userRepository.findById(user.getId());

        if (!userOptional.isPresent()) {
            // Handle case where user with given ID does not exist
            return "login"; // redirect to login
        }

        // Convert User to SecurityUser (SecurityUser extends UserDetails so it's ok?)
        User updatedUser = userOptional.get();
        SecurityUser updatedSecurityUser = new SecurityUser(updatedUser); // Create SecurityUser from User

        // Update user information in the session
        ((UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication())
                .setDetails(updatedSecurityUser);

        // Add updated user information to the model
        model.addAttribute("user", updatedSecurityUser);

        return "myaccount";
    }
    // }

}
