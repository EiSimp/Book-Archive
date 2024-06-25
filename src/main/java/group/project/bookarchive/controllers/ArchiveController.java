package group.project.bookarchive.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ArchiveController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("login");
    }

    @GetMapping("/archives/view")
    public String getAllUsers() {
        return "homepage";
    }

    @GetMapping("/homepage")
    public String homepage() {
        return "homepage";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/login")
    public String getLogin(Model model, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("session_user");
        if (user == null) {
            return "login";
        } else {
            model.addAttribute("user", user);
            return "users/protected";
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

    @GetMapping("/profile")
    public String profile() {
        return "profilesetting";
    }

    @PostMapping("/forgot")
    public String forgotPassword(@RequestBody String entity) {
        // TODO: process POST request

        return entity;
    }

    @PostMapping("/signup")
    public String addUser(@RequestParam Map<String, String> newUser, HttpServletResponse response) {
        String newName = newUser.get("username");
        String newPw = newUser.get("password");
        userRepository.save(new User(newPw, newName));
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "login";
    }

    // User Login
    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request,
            HttpSession session) {
        // processing login
        String name = formData.get("username");
        String pwd = formData.get("password");
        List<User> userlist = userRepository.findByUsernameAndPassword(name, pwd);
        if (userlist.isEmpty()) {
            return "login";
        } else {
            // success
            User user = userlist.get(0);
            request.getSession().setAttribute("session_user", user);
            model.addAttribute("user", user);
            return "homepage";
        }
    }
}
