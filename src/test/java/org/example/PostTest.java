package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;  // נכון
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostTest {
    @Test
    public void postConstructorTest(){
        // Create a post & user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        Post postTest = new Post("TitleTest","ContentTest",user);
        // Check the Constructor fields
        assertEquals("TitleTest",postTest.getTitle());
        assertEquals("ContentTest",postTest.getContent());
        assertEquals(user,postTest.getUser());
        assertNotNull(postTest.getCreatedDate());
    }

    @Test
    public void getTitleTest() {
        // Create a post & user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        Post postTest = new Post("TitleTest","ContentTest",user);
        // Check the function getTitle
        assertEquals("TitleTest",postTest.getTitle());
    }
    @Test
    public void setTitleTest() {
        // Create a post & user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        Post postTest = new Post("TitleTest","ContentTest",user);
        //call the function setTitle
        postTest.setTitle("newTitleTest");
        // Check the setTitle
        assertEquals("newTitleTest",postTest.getTitle());
    }

    @Test
    public void getContent() {
        // Create a post & user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        Post postTest = new Post("TitleTest","ContentTest",user);
        // Check the function getContent
        assertEquals("ContentTest",postTest.getContent());
    }
    @Test
    public void setContentTest() {
        // Create a post & user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        Post postTest = new Post("TitleTest","ContentTest",user);
        //call the function setContent
        postTest.setContent("newContentTest");
        // Check the setContent
        assertEquals("newContentTest",postTest.getContent());
    }

    @Test
    public void getCreatedDateTest() {
        // Create a post & user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        Post postTest = new Post("TitleTest","ContentTest",user);
        // Check the setContent
        assertNotNull(postTest.getCreatedDate());
        assertTrue(postTest.getCreatedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(postTest.getCreatedDate().isAfter(LocalDateTime.now().minusSeconds(1)));    }
    @Test
    public void getUserTest() {
        // Create a post & user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        Post postTest = new Post("TitleTest","ContentTest",user);
        // Check the function getUser
        assertEquals(user,postTest.getUser());
    }

    @Test
    public void setUserTest( ) {
        // Create a post & user for testing
        User user = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        Post postTest = new Post("TitleTest","ContentTest",user);
        //call the function setUser with new user
        User newUserTest = new User("TestName","TestLastName",22,
                "testPassword123","TestEmail@gmail.com");
        postTest.setUser(newUserTest);
        // Check the setUser
        assertEquals(newUserTest,postTest.getUser());
    }

}
