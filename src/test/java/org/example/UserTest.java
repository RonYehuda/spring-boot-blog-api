package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void userConstructorTest(){
        // Create a user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        // Check the Constructor fields
        assertEquals("TestName",user.getName());
        assertEquals("TestLastName",user.getLastName());
        assertEquals(22,user.getAge());
        assertTrue(user.getPosts().isEmpty());

    }

    @Test
    public void getNameTest(){
        // Create a user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        // call the function getName
        String result = user.getName();
        // Check the result
        assertEquals("TestName",result);
    }
    @Test
    public void setNameTest(){
        // Create a user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        // call the function setName
        user.setName("newTestName");

        // Check the result
        assertEquals("newTestName",user.getName());
    }

    @Test
    public void getLastNameTest(){
        // Create a user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        // call the function getLastName
        String result = user.getLastName();
        // Check the result
        assertEquals("TestLastName",result);
    }
    @Test
    public void setLastNameTest(){
        // Create a user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        // call the function setLastName
        user.setLastName("newTestLastName");
        // Check the result
        assertEquals("newTestLastName",user.getLastName());
    }

    @Test
    public void getAgeTest(){
        // Create a user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        // call the function getAge
        int result = user.getAge();
        // Check the result
        assertEquals(22,result);
    }
    @Test
    public void setAgeTest(){
        // Create a user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        // call the function setAge
        user.setAge(20);
        // Check the result
        assertEquals(20,user.getAge());
    }

    @Test
    public void addPostTest(){
        // Create a user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        Post postTest = new Post("Title Test","Content Test",user);
        // call the function AddPost
        user.addPost(postTest);
        // Check the post was added
        assertEquals(1, user.getPosts().size());
        assertTrue(user.getPosts().contains(postTest));
        assertEquals(user, postTest.getUser());  // Check bidirectional relationship
    }
}
