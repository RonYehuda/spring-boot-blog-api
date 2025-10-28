package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)  // ← החלף בזה!

public class UserControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    // Helper method to create valid JWT token for testing
    private String createValidToken() {
        return jwtUtil.generateToken("admin@example.com", Role.ADMIN);
    }

    @Test
    public void testGetAllUser() throws Exception {
        String token = createValidToken();  // make token

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token))  // add token
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateUser() throws Exception {
        String token = createValidToken();
        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test name\",\"lastName\":\"Test last name\",\"age\":33,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Created user")))
                .andExpect(content().string(containsString("Test name")));
    }

    @Test
    public void testCreateUserWithInvalidData()throws Exception{
        String token = createValidToken();
        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"lastName\":\"cohen\",\"age\":15,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors",hasSize(3)))
                .andExpect(jsonPath("$.errors",hasItem(containsString("Name cant be blank"))))
                .andExpect(jsonPath("$.errors",hasItem(containsString("Name must be between 2 to 50 chars"))))
                .andExpect(jsonPath("$.errors",hasItem(containsString("Minimum age is 18"))));
    }
    @Test
    public void testUpdateUser()throws Exception{
        String token = createValidToken();
        //Create user
        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test name\",\"lastName\": \"Test last name\",\"age\":30,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"));

        //Update the user
        mockMvc.perform(put("/users/{id}",1L).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("{\"name\": \"new Test name\",\"lastName\": \"new Test last name\",\"age\":30,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("is updated")))
                .andExpect(content().string(containsString("new Test name")))
                .andExpect(content().string(containsString("new Test last name")));
    }

    @Test
    public void testUpdateUser_InvalidData()throws Exception{
        String token = createValidToken();
        //Create illegal user
        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"new Test name\",\"lastName\": \"new Test last name\",\"age\":30,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"));

        //Update the user
        mockMvc.perform(put("/users/{id}",1L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\",\"lastName\": \"\",\"age\":15,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors",hasSize(5)))
                .andExpect(jsonPath("$.errors",hasItem(containsString("Name cant be blank"))))
                .andExpect(jsonPath("$.errors",hasItem(containsString("Name must be between 2 to 50 chars"))))
                .andExpect(jsonPath("$.errors",hasItem(containsString("Last Name cant be blank"))))
                .andExpect(jsonPath("$.errors",hasItem(containsString("Last Name must be between 2 to 50 chars"))))
                .andExpect(jsonPath("$.errors",hasItem(containsString("Minimum age is 18"))));
    }
    @Test
    public void testUpdateUser_UserNotFound()throws Exception{
        String token = createValidToken();
        //Create user
        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test name\",\"lastName\": \"Test last name\",\"age\":30,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"));

        // Try to update non-existent user (ID=999) - should return 404
        mockMvc.perform(put("/users/{id}",999L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"new Test name\",\"lastName\": \"new Test last name\",\"age\":30,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser()throws Exception{
        String token = createValidToken();
        //Create user
        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"test name\",\"lastName\":\"test last name\",\"age\":30,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"));
        //delete user
        mockMvc.perform(delete("/users/{id}",1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("deleted")));
    }

    @Test
    public void testDeleteUser_UserNotFound()throws Exception{
        String token = createValidToken();
        //Create user
        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"test name\",\"lastName\":\"test last name\",\"age\":30,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"));
        // Try to delete non-existent user (ID=999) - should return 404
        mockMvc.perform(delete("/users/{id}",999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindUserById()throws Exception{
        String token = createValidToken();
        //Create user
        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"test name\",\"lastName\":\"test last name\",\"age\":30,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"));
        //find the user by the id
        mockMvc.perform(get("/users/{id}",1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
    @Test
    public void testFindUserById_UserNotFound()throws Exception{
        String token = createValidToken();
        //Create user
        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"test name\",\"lastName\":\"test last name\",\"age\":30,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"));
        // Try to get non-existent user (ID=999) - should return 404
        mockMvc.perform(get("/users/{id}",999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindUserByAge()throws Exception{
        String token = createValidToken();
        //Create user
        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"test name\",\"lastName\":\"test last name\",\"age\":25,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"));
        //find the user by the age
        mockMvc.perform(get("/users/age/{age}",25)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
    @Test
    public void testFindUserByAge_UserNotFound()throws Exception{
        String token = createValidToken();
        //Create user
        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"test name\",\"lastName\":\"test last name\",\"age\":25,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"));
        // Try to get users with age 20 (no users with this age) - should return 404
        mockMvc.perform(get("/users/age/{age}",20)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindByAgeGreaterThan()throws Exception{
        String token = createValidToken();
        //Create user
        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"test name\",\"lastName\":\"test last name\",\"age\":30,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"));
        //find the user with age above than 25
        mockMvc.perform(get("/users/age-above/{age}",25)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
    @Test
    public void testFindByAgeGreaterThan_UserNotFound()throws Exception{
        String token = createValidToken();
        //Create user
        mockMvc.perform(post("/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"test name\",\"lastName\":\"test last name\",\"age\":20,\"password\":\"testPassword123\",\"email\":\"test@example.com\"}"));
        // Try to get users with age above than 25 (no users with this age) - should return 404
        mockMvc.perform(get("/users/age-above/{age}",25)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }


}