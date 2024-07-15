package group.project.bookarchive;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import group.project.bookarchive.controllers.ArchiveController;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.UserRepository;
import group.project.bookarchive.services.MailService;
import group.project.bookarchive.services.UserService;

@WebMvcTest(ArchiveController.class)
public class ArchiveControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private MailService mailService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Example admin role for testing
    public void testGetAllUsers() throws Exception {
        // Mock data
        User user1 = new User("user1", "password1", "hey@gmail.com");
        User user2 = new User("user2", "password2", "hey@gmail.com");
        List<User> userList = Arrays.asList(user1, user2);

        // Mock behavior of service.listAll() to return userList
        when(userService.listAll()).thenReturn(userList);

        // Perform GET request and verify expectations
        mockMvc.perform(get("/admin/users"))
               .andExpect(status().isOk())
               .andExpect(view().name("users"))
               .andExpect(model().attributeExists("listUsers"))
               .andExpect(model().attribute("listUsers", userList));
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

    // ... find why user is null

    // @Test
    // @WithMockUser(username = "testuser", roles = {"USER"})
    // public void testProfileSetting() throws Exception {
    //     mockMvc.perform(get("/profilesetting"))
    //             .andExpect(status().isOk())
    //             .andExpect(MockMvcResultMatchers.view().name("profilesetting"));
    // }

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
