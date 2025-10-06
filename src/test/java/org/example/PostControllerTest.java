package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PostControllerTest {

    @Autowired PostController postController;
    @Autowired UserController userController;

    @Test
    public void newPostTest(){
        //Create new user for testing
        User userTest = new User("Test Name", "TestLastName",20,
                "testPassword123","TestEmail@gmail.com");
        ResponseEntity responseUser = userController.createUser(userTest);

        // Create a post for testing
        Post post = new Post("TestTitle","TestContent",userTest);

        // Call the function
        ResponseEntity<Post> response = postController.newPost(userTest.getId(),post);

        // Check that the response is correct
        assertEquals(200, response.getStatusCode().value());
        assertEquals("TestTitle", response.getBody().getTitle());
        assertEquals("TestContent", response.getBody().getContent());
        assertNotNull(response.getBody().getUser());
    }


}
