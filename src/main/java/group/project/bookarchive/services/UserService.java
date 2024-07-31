package group.project.bookarchive.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import group.project.bookarchive.models.User;
import group.project.bookarchive.models.UserRole;
import group.project.bookarchive.repositories.UserRepository;
import group.project.bookarchive.repositories.UserRoleRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    UserRoleRepository roleRepo;

    public List<User> listAll() {
        return userRepo.findAll();
    }

    public boolean usernameExists(String username) {
        return userRepo.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepo.existsByEmail(email);
    }

    public void registerDefaultUser(User user) {
        UserRole roleUser = roleRepo.findByName("User");
        user.addRole(roleUser);

        userRepo.save(user);
    }


}
