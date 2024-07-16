package group.project.bookarchive.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.UserRepository;
import group.project.bookarchive.security.SecurityUser;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addUserToModel(Model model, @AuthenticationPrincipal SecurityUser user) {
        if (user != null) {
            // get the user by id to grab new changes
            Optional<User> optionalUser = userRepository.findById(user.getId());

            User updatedUser = optionalUser.get();
            SecurityUser updatedSecurityUser = new SecurityUser(updatedUser);
            
            model.addAttribute("user", updatedSecurityUser);
            model.addAttribute("loggedinUser", updatedSecurityUser.getUsername());
            model.addAttribute("roles", updatedSecurityUser.getAuthorities());
            model.addAttribute("profilePicture", updatedSecurityUser.getProfilePhoto());
        }
    }
}