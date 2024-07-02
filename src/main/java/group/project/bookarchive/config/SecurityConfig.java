package group.project.bookarchive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import group.project.bookarchive.security.SecurityUser;
import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests((authz) -> authz
                                                .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD)
                                                .permitAll()
                                                .requestMatchers("/login", "/signup", "/forgot", "/stylesheet/**",
                                                                "/javascript/**", "/images/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .anyRequest().hasAnyRole("USER", "ADMIN"))
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/homepage?loginsuccess", true)
                                                .successHandler((request, response, authentication) -> {
                                                        SecurityUser user = (SecurityUser) authentication
                                                                        .getPrincipal();
                                                        if (user.getTempPwd()) {
                                                                response.sendRedirect("/passwordchange");
                                                        } else {
                                                                response.sendRedirect("/homepage");
                                                        }
                                                })
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
