package group.project.bookarchive.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class archiveController {
    @GetMapping("/archives/view")
    public String getAllUsers(){
         return "/homepage";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/homepage")
    public String homepage() {
        return "homepage";
    }

    @GetMapping("/signup")
    public String signup(){
        return "signup";
    }
}
