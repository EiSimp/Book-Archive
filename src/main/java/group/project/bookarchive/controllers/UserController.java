package group.project.bookarchive.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import group.project.bookarchive.models.User;
import group.project.bookarchive.models.UserUpdateRequest;
import group.project.bookarchive.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
//Each method returns a domain object and not a view
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    //Returns a list of all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    //Get user by ID
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
    }
    //Creates a new user
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public User createUser(@RequestParam String username, @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return userRepository.save(user);
    }
    
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id,
                        @RequestBody UserUpdateRequest request) {
        User user = userRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setBio(request.getBio());
        return userRepository.save(user);
    }


    //Deletes user
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id, HttpServletRequest request) {
        userRepository.deleteById(id);
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        return "redirect:/login";
    }
}
