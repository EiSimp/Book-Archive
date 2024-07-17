package group.project.bookarchive;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import group.project.bookarchive.models.User;

public class UserTest {

    @Test
    public void testFullConstructor() {
        User user = new User("john_doe", "password123", "Sample bio");
        assertEquals("john_doe", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("Sample bio", user.getBio());
    }

    @Test
    public void testConstructorWithoutBio() {
        User user = new User("jane_smith", "pass456", "fakeemail@gmail.com");
        assertEquals("jane_smith", user.getUsername());
        assertEquals("pass456", user.getPassword());
        assertEquals("fakeemail@gmail.com", user.getEmail());
        assertEquals(null, user.getBio());
    }

    @Test
    public void testSettersAndGetters() {
        User user = new User();
        
        user.setUsername("new_user");
        assertEquals("new_user", user.getUsername());
        
        user.setPassword("new_password");
        assertEquals("new_password", user.getPassword());
        
        user.setBio("New bio");
        assertEquals("New bio", user.getBio());

        user.setEmail("fakeemail1@gmail.com");
        assertEquals("fakeemail1@gmail.com", user.getEmail());


        user.setUsername("old_user");
        assertEquals("old_user", user.getUsername());
        
        user.setPassword("old_password");
        assertEquals("old_password", user.getPassword());
        
        user.setBio("Old bio");
        assertEquals("Old bio", user.getBio());

        user.setEmail("fakeemail2@gmail.com");
        assertEquals("fakeemail2@gmail.com", user.getEmail());

    }


}