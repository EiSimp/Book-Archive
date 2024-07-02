package group.project.bookarchive.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import group.project.bookarchive.security.SecurityUser;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addUserToModel(Model model, @AuthenticationPrincipal SecurityUser user) {
        if (user != null) {
            model.addAttribute("user", user);
        }
    }
}