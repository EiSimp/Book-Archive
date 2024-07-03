package group.project.bookarchive.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
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
import group.project.bookarchive.services.MailService;
import group.project.bookarchive.services.UserService;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @GetMapping("/profilesetting")
    public String profileSetting() {
        return "profilesetting";
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
        mailService.sendPwdMail(mailDto);
        System.out.println("sent email");
        return "login";
    }

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

        service.registerDefaultUser(userRepository
                .save(new User(form.getUsername(), new BCryptPasswordEncoder().encode(form.getPassword()))));
        ;
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

}
