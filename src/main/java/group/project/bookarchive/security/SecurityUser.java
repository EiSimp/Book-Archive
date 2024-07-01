package group.project.bookarchive.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import group.project.bookarchive.models.User;

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

    public boolean isTempPwd() {
        return decorated.getTempPwd();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_USER");
    }

}
