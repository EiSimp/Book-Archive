package group.project.bookarchive.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import group.project.bookarchive.models.MailDTO;
import group.project.bookarchive.models.SignupFormDTO;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.UserRepository;
import group.project.bookarchive.security.SecurityUser;
import group.project.bookarchive.services.BookshelfService;
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
    private BookshelfService bookshelfService;

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

    @GetMapping("/bookclubdetail")
    public String bookclubDetail() {
        return "bookclubdetail";
    }

    @GetMapping("/homepage")
    public String homepage(Model model, @AuthenticationPrincipal SecurityUser user) {
        //model.addAttribute("bookshelves", bookshelfService.getAllBookshelvesByUser());

        model.addAttribute("bookshelves", bookshelfService.getAllBookshelvesByUserId(user.getId()));
        return "homepage";
    }

    @GetMapping("/bookshelf/details/{id}")
    public String getBookshelfDetailsPage(@PathVariable Long id, Model model) {
        model.addAttribute("bookshelfId", id);
        return "bookshelfdetail";
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
    public String profileSetting(Model model, @AuthenticationPrincipal SecurityUser user) {
        // Fetch updated user data from the database based on ID
        Optional<User> userOptional = userRepository.findById(user.getId());

        if (userOptional.isPresent()) {
            // Convert User to SecurityUser
            User updatedUser = userOptional.get();
            SecurityUser updatedSecurityUser = new SecurityUser(updatedUser); // Create SecurityUser from User

            // Update user information in the session
            UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            authentication.setDetails(updatedSecurityUser);

            // Add updated user information to the model
            model.addAttribute("user", updatedSecurityUser);

            // Clear authentication context to force reload of user details
            SecurityContextHolder.clearContext();

            return "profilesetting"; // Return the correct template name
        } else {
            return "login";
        }
    }

    @GetMapping("/header")
    public String getHeader() {
        return "fragments/header.html";
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
            return "signup";
        }

        User user = new User(form.getUsername(), new BCryptPasswordEncoder().encode(form.getPassword()),
                form.getEmail());

        service.registerDefaultUser(userRepository.save(user));

        // Call the method to create default bookshelves for the user
        bookshelfService.createDefaultBookshelvesForUser(user);

        return "redirect:/login?signupsuccess";
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = service.usernameExists(username);// logic to check if username exists
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = service.emailExists(email);// logic to check if email exists
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
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
        if (user == null) {
            return "redirect:/login"; // Redirect to login page if user is not authenticated.
        } else {
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
            // Clear authentication context to force reload of user details
            SecurityContextHolder.clearContext();

            return "myaccount";
        }
    }

    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam Long id,
            @RequestParam String profilePhoto,
            @RequestParam String bio) {
        // Fetch user by id
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update user's profile fields
        user.setProfilePhoto(profilePhoto);
        user.setBio(bio);

        // Save updated user
        userRepository.save(user);

        // Redirect to a success page or return a success message
        return "redirect:/profilesetting?success"; // Redirect to profile page after update
    }

    public static String fetchJsonFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            throw new IOException("Failed to fetch content from URL. Response code: " + responseCode);
        }
    }

}
