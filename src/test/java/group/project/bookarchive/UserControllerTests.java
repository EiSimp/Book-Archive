package group.project.bookarchive;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import group.project.bookarchive.controllers.UserController;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.UserRepository;

@WebMvcTest(UserController.class)
public class UserControllerTests {

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
        // Mock data
        User user1 = new User("user1", "password1"); // User with empty bio
        User user2 = new User("user2", "password2", "User 2's bio");
        User user3 = new User("user3", "password1", ""); // User with empty bio
        List<User> userList = Arrays.asList(user1, user2, user3);

        // Mock repository behavior
        when(userRepository.findAll()).thenReturn(userList);

        // Perform the GET request and validate the response
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].bio").value(Matchers.anyOf(Matchers.nullValue(), Matchers.emptyString()))) // Handle null/empty bio
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value("user2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].bio").value("User 2's bio")) // Ensure bio is not empty
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].username").value("user3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].bio").value("")); // Handle empty bio
    }
}
