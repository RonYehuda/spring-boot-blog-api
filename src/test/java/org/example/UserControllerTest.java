package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    public void testCreateUser() {
        // Create a user for testing
        User user = new User("TestName", "TestLast", 25,
                "testPassword123","TestEmail@gmail.com");

        // Call the function
        ResponseEntity<String> response = userController.createUser(user);

        // Check that the response is correct
        assertEquals(201, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Created user"));
        assertTrue(response.getBody().contains("TestName"));
    }

    @Test
    public void testUpdateUser(){
        //Create new user for testing
        User user = new User("TestName1","TestLastName1",20,
                "testPassword123","TestEmail@gmail.com");
        userController.createUser(user);

        //Create user for update data
        User updateData  = new User("TestNameUpdate","TestLastUpdate",23,
                "testPassword123","TestEmail@gmail.com");

        // Call the function - updateUser
        ResponseEntity<String> response = userController.updateUser(user.getId(),updateData );

        // Check that the response is correct
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("is updated"));
        assertTrue(response.getBody().contains("TestNameUpdate"));
    }

    @Test
    public void testDeleteUser(){
        //Create new user for testing
        User user = new User("TestName","TestLast", 19,
                "testPassword123","TestEmail@gmail.com");
        userController.createUser(user);

        // Call the function - deleteUser
        ResponseEntity<String> response = userController.deleteUser(user.getId());

        //Check that the response is correct
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("deleted"));
    }

}