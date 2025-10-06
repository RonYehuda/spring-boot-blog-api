package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Rollback
public class PostControllerMockMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testNewPost()throws Exception{
        //Create user (will get ID=1)
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test name\",\"lastName\": \"Test last name\",\"age\":30,\"password\":\"testPassword123\"}"));
        //Create post for user ID=1
        mockMvc.perform(post("/users/{userId}/posts", 1L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test title\",\"content\":\"test content\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("test title")))
                .andExpect(jsonPath("$.content", is("test content")));
    }
    @Test
    public void testNewPost_UserNotFound()throws Exception{
        //Create post with no user
        mockMvc.perform(post("/users/{userId}/posts", 999L).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"test title\",\"content\":\"test content\"}"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testUserAllPosts()throws Exception{
        //Create user (will get ID=1)
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test name\",\"lastName\": \"Test last name\",\"age\":30,\"password\":\"testPassword123\"}"));
        //Create 3 posts for user ID=1
        mockMvc.perform(post("/users/{userId}/posts", 1L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test 1 title\",\"content\":\"test 1 content\"}"));
        mockMvc.perform(post("/users/{userId}/posts", 1L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test 2 title\",\"content\":\"test 2 content\"}"));
        mockMvc.perform(post("/users/{userId}/posts", 1L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test 3 title\",\"content\":\"test 3 content\"}"));
        //return all post by user with id = 1L
        mockMvc.perform(get("/users/{userId}/posts",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].title", is("test 1 title")))
                .andExpect(jsonPath("$[0].content", is("test 1 content")))
                .andExpect(jsonPath("$[1].title", is("test 2 title")))
                .andExpect(jsonPath("$[1].content", is("test 2 content")))
                .andExpect(jsonPath("$[2].title", is("test 3 title")))
                .andExpect(jsonPath("$[2].content", is("test 3 content")));
    }

    @Test
    public void testReturnAllPost()throws Exception{
        //Create user (will get ID=1)
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test name\",\"lastName\": \"Test last name\",\"age\":30,\"password\":\"testPassword123\"}"));
        //Create user (will get ID=2)
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test 2 name\",\"lastName\": \"Test 2 last name\",\"age\":30,\"password\":\"testPassword123\"}"));
        //Create 1 posts for user ID=1
        mockMvc.perform(post("/users/{userId}/posts", 1L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test 1 title\",\"content\":\"test 1 content\"}"));
        //Create 1 posts for user ID=2
        mockMvc.perform(post("/users/{userId}/posts", 2L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test 2 title\",\"content\":\"test 2 content\"}"));
        //return all posts by all users
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("test 1 title")))
                .andExpect(jsonPath("$[0].content", is("test 1 content")))
                .andExpect(jsonPath("$[1].title", is("test 2 title")))
                .andExpect(jsonPath("$[1].content", is("test 2 content")));
    }

    @Test
    public void testFindByTitle()throws Exception{
        //Create user (will get ID=1)
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test name\",\"lastName\": \"Test last name\",\"age\":30,\"password\":\"testPassword123\"}"));
        //Create 1 posts for user ID=1
        mockMvc.perform(post("/users/{userId}/posts", 1L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test 1 title\",\"content\":\"test 1 content\"}"));
        //find the post by the title
        mockMvc.perform(get("/posts/title/{title}","test 1 title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title",is("test 1 title")));
    }
    @Test
    public  void testFindByTitleContaining()throws Exception{
        //Create user (will get ID=1)
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test name\",\"lastName\": \"Test last name\",\"age\":30,\"password\":\"testPassword123\"}"));
        //Create 1 posts for user ID=1
        mockMvc.perform(post("/users/{userId}/posts", 1L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test 1 title\",\"content\":\"test 1 content\"}"));
        //try to find by title containing
        mockMvc.perform(get("/posts/search/{keyword}","test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("test 1 title")));
    } @Test
    public  void testFindByTitleContaining_PostNotFound()throws Exception{
        //Create user (will get ID=1)
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test name\",\"lastName\": \"Test last name\",\"age\":30,\"password\":\"testPassword123\"}"));
        //Create 1 posts for user ID=1
        mockMvc.perform(post("/users/{userId}/posts", 1L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test 1 title\",\"content\":\"test 1 content\"}"));
        //try to find by title containing
        mockMvc.perform(get("/posts/search/{keyword}","sdfs"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testFindByUserId()throws Exception{
        //Create user (will get ID=1)
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test name\",\"lastName\": \"Test last name\",\"age\":30,\"password\":\"testPassword123\"}"));
        //Create user (will get ID=2)
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test 2 name\",\"lastName\": \"Test 2 last name\",\"age\":30,\"password\":\"testPassword123\"}"));
        //Create 1 posts for user ID=1
        mockMvc.perform(post("/users/{userId}/posts", 1L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test 1 title\",\"content\":\"test 1 content\"}"));
        //Create 1 posts for user ID=2
        mockMvc.perform(post("/users/{userId}/posts", 2L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test 2 title\",\"content\":\"test 2 content\"}"));
        //get all post by user (id = 1L)
        mockMvc.perform(get("/posts/user/{userId}",1L))
                .andExpect(status().isOk());
    }
    @Test
    public void testFindByUserId_UserNotFound()throws Exception{
        //Create user (will get ID=1)
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test name\",\"lastName\": \"Test last name\",\"age\":30,\"password\":\"testPassword123\"}"));
        //Create user (will get ID=2)
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test 2 name\",\"lastName\": \"Test 2 last name\",\"age\":30,\"password\":\"testPassword123\"}"));
        //Create 1 posts for user ID=1
        mockMvc.perform(post("/users/{userId}/posts", 1L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test 1 title\",\"content\":\"test 1 content\"}"));
        //Create 1 posts for user ID=2
        mockMvc.perform(post("/users/{userId}/posts", 2L).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"test 2 title\",\"content\":\"test 2 content\"}"));
        //try to get all post of user that no exist
        mockMvc.perform(get("/posts/user/{userId}",3L))
                .andExpect(status().isNotFound());
    }
}
