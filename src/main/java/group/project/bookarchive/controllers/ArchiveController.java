package group.project.bookarchive.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ArchiveController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("/login");
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

    @PostMapping("/forgot")
    public String forgotPassword(@RequestBody String entity) {
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

    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request,
                        HttpSession session) {
        String name = formData.get("username");
        String pwd = formData.get("password");
        List<User> userlist = userRepository.findByUsername(name);
        if (userlist.isEmpty() || !new BCryptPasswordEncoder().matches(pwd, userlist.get(0).getPassword())) {
            return "login";
        } else {
            User user = userlist.get(0);
            request.getSession().setAttribute("session_user", user);
            model.addAttribute("user", user);
            return "homepage";
        }
    }
}
