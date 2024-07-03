package group.project.bookarchive.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import group.project.bookarchive.models.User;
import group.project.bookarchive.models.UserRole;

public class SecurityUser implements UserDetails {
    private final User decorated;

    public SecurityUser(final User decorated) {
        this.decorated = decorated;
    }

    @Override
    public String getUsername() {
        return decorated.getUsername();
    }

    @Override
    public String getPassword() {
        return decorated.getPassword();
    }


    public String getBio() {
        return decorated.getBio();
    }

    
    public Long getId() {
        return decorated.getId();
    }


    
    public boolean getTempPwd() {
        return decorated.getTempPwd();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<UserRole> roles = decorated.getRoles();
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
                .collect(Collectors.toList());
    }

}
