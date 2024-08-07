package group.project.bookarchive.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import group.project.bookarchive.security.SecurityUser;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
                private String urlPrefix;
                private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

                public LoginFailureHandler(String urlPrefix) {
                        this.urlPrefix = urlPrefix;
                }

                @Override
                public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                    AuthenticationException exception) throws IOException, ServletException {
                        saveException(request, exception);
                        String username = request.getParameter(
                                UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY);
                        String redirectUrl = urlPrefix + username;
                        // Redirect:
                        redirectStrategy.sendRedirect(request, response, redirectUrl);
                }
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .authorizeHttpRequests((authz) -> authz
                                .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD)
                                .permitAll()
                                .requestMatchers("/login", "/signup", "/forgot", "/myaccount", "/profilesetting",
                                        "/stylesheet/**", "/javascript/**", "/images/**",
                                        "/check-username", "/check-email")
                                .permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/bookshelf-items/**").authenticated()
                                .requestMatchers("/messages/**").authenticated()
                                .requestMatchers("/chat-websocket/**").permitAll()  // Allow WebSocket endpoint
                                .requestMatchers("/bookclubs/**").authenticated()
                                .anyRequest().hasAnyRole("USER", "ADMIN"))
                        .formLogin(form -> form
                                .loginPage("/login")
                                .defaultSuccessUrl("/homepage?loginsuccess", true)
                                .successHandler((request, response, authentication) -> {
                                        SecurityUser user = (SecurityUser) authentication.getPrincipal();
                                        if (user.getTempPwd()) {
                                                response.sendRedirect("/passwordchange");
                                        } else {
                                                response.sendRedirect("/homepage");
                                        }
                                })
                                .failureHandler(new LoginFailureHandler("/login?error&username="))
                                .permitAll())
                        .logout((logout) -> logout.logoutSuccessUrl("/login?logoutsuccess"))
                        .passwordManagement(customizer -> customizer.changePasswordPage("/change-password"))
                        .csrf(csrf -> csrf
                                .csrfTokenRepository(csrfTokenRepository())
                                .ignoringRequestMatchers("/chat-websocket/**"));  // Disable CSRF for WebSocket endpoint

                return http.build();
        }

        @Bean
        public CsrfTokenRepository csrfTokenRepository() {
                HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
                repository.setHeaderName("X-XSRF-TOKEN");
                return repository;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
