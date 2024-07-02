package group.project.bookarchive.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    UserRoleRepository roleRepo;

    public List<User> listAll() {
        return userRepo.findAll();
    }

    public void registerDefaultUser(User user) {
        UserRole roleUser = roleRepo.findByName("User");
        user.addRole(roleUser);

        userRepo.save(user);
    }

}
