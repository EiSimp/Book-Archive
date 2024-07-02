package group.project.bookarchive;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import group.project.bookarchive.controllers.ArchiveController;
import group.project.bookarchive.repositories.UserRepository;


@WebMvcTest(ArchiveController.class)
public class ArchiveControllerTests {

    private static final Logger logger = Logger.getLogger(UserControllerTests.class.getName());

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    // For authentication
    @BeforeEach
    public void setup() {
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testGetAllUsers() throws Exception {
    mockMvc.perform(get("/archives/view"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("homepage"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testHomepage() throws Exception {
        mockMvc.perform(get("/homepage"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("homepage"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testSignup() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("signup"));
    }

    // @Test
    // @WithMockUser(username = "testuser", roles = {"USER"})
    // public void testGetLogin_authenticated() throws Exception {
    //     mockMvc.perform(get("/login"))
    //             .andExpect(status().isOk())
    //             .andExpect(status().is2xxSuccessful());
    //.andExpect(redirectedUrlPattern("/homepage*")); // Check if the URL starts with /homepage
    // }

    // @Test
    // public void testGetLogin_unauthenticated() throws Exception {
    //     mockMvc.perform(get("/login"))
    //             .andExpect(status().isOk())
    //             .andExpect(MockMvcResultMatchers.view().name("login"));
    // }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testForgotpwd() throws Exception {
        mockMvc.perform(get("/forgot"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("forgotpwd"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testBookclubs() throws Exception {
        mockMvc.perform(get("/bookclubs"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("bookclubs"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testMyrecord() throws Exception {
        mockMvc.perform(get("/myrecord"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("myrecord"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testProfileSetting() throws Exception {
        mockMvc.perform(get("/profilesetting"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("profilesetting"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testChangePassword() throws Exception {
        mockMvc.perform(get("/passwordchange"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("passwordchange"));
    }



    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testChangePassword_Post_PasswordsDoNotMatch() throws Exception {
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";
        String confirmPassword = "confirmPassword"; // Different from newPassword
        
        mockMvc.perform(MockMvcRequestBuilders.post("/change-password")
            .param("current-password", currentPassword)
            .param("new-password", newPassword)
            .param("confirm-password", confirmPassword)
            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("passwordchange"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
            .andExpect(MockMvcResultMatchers.model().attribute("error", "New password and confirmation do not match"));

        verifyNoInteractions(userRepository); // No repository interaction expected in this case
    }



}

