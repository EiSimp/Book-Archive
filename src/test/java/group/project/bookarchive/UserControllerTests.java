package group.project.bookarchive;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
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
        User user1 = new User("user1", "password1", "fakeemail@gmail.com"); 
        User user2 = new User("user2", "password2", "fakeemail2@gmail.com");
        User user3 = new User("user3", "password1", "fakeemail3@gmail.com"); 
        List<User> userList = Arrays.asList(user1, user2, user3);

        // Mock repository behavior
        when(userRepository.findAll()).thenReturn(userList);

        // Perform the GET request and validate the response
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].bio").value(Matchers.anyOf(Matchers.nullValue(), Matchers.emptyString()))) // Handle null/empty bio
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("fakeemail@gmail.com")) // Ensure emails are not empty
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value("user2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value("fakeemail2@gmail.com")) // Ensure emails are not empty
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].username").value("user3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].email").value("fakeemail3@gmail.com")); 
        
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testCreateUser() throws Exception {
        // Mock data
        String username = "newuser";
        String password = "newpassword";
        String email = "fakeemail@gmail.com";
        User newUser = new User(username, password, email);

        // Mock repository behavior
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Perform the POST request with csrf token, and validate the response
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "newuser")
                .param("password", "newpassword")
                .param("email", email)
                .with(csrf())) // Include CSRF token
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value(password));
       
        // Verify repository method was called once with correct parameter
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testUpdateUser() throws Exception {
        // Mock data
        // First created user will have an ID of 1
        Long userId = 1L;
        User existingUser = new User("testuser", "password", "fakeemail@gmail.com");

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Perform the PUT request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.put("/user/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"updateduser\", \"password\": \"updatedpassword\", \"email\": \"updatedemail@gmail.com\", \"bio\": \"Updated bio\"}")
                .with(csrf())) // Include CSRF token
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updatedemail@gmail.com"))
                .andExpect(jsonPath("$.bio").value("Updated bio"));
                
        // Verify repository methods were called once with correct parameters
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testGetUserById() throws Exception {
        // Mock data
        // First created user will have an ID of 1
        Long userId = 1L;
        User user = new User("testuser", "password", "fakeemail@gmail.com");

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Perform the GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", userId))
                 .andExpect(status().isOk())
                 .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testuser"))
                 .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("password"));

        // Verify repository method was called once with correct parameter
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testDeleteUser() throws Exception {
        // Mock data
        // First created user will have an ID of 1
        Long userId = 1L;
        User existingUser = new User("testuser", "password", "fakeemail@gmail.com");

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Perform the DELETE request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/{id}", userId)
                .with(csrf())) // Include CSRF token
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("redirect:/login")); // Check redirect URL

        // Verify repository method was called once with correct parameter
        verify(userRepository, times(1)).deleteById(userId);
        verifyNoMoreInteractions(userRepository);
    }



}


