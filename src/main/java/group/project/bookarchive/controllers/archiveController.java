package group.project.bookarchive.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class archiveController {
    @GetMapping("/archives/view")
    public String getAllUsers(){
         return "archives/homepage";
    }
}
