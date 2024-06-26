package group.project.bookarchive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
                        .requestMatchers("/login", "/signup", "/forgot", "/stylesheet/**", "/javascript/**", "/images/**").permitAll()
                        .anyRequest().hasRole("USER"))
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/homepage?loginsuccess", true)
                        .permitAll())
                .logout((logout) -> logout
                        .logoutSuccessUrl("/login?logoutsuccess"))
                .passwordManagement(customizer -> customizer
                        .changePasswordPage("/change-password"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
