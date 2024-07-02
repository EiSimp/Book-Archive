package group.project.bookarchive.services;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.UserRepository;
import group.project.bookarchive.security.SecurityUser;

@Service
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        final Optional<User> user = this.repository.findByUsername(username);

        return user.map(SecurityUser::new).orElseThrow(() -> new UsernameNotFoundException("Unknown user " + username));
    }

    public JpaUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

}
