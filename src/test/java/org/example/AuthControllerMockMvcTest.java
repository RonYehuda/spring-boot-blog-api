package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // ← Every test runs in a transaction
@Rollback // ← Rollback after every test
public class AuthControllerMockMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRegister_Success() throws Exception{
        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"lastName\":\"User\",\"age\":25,\"password\":\"password123\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }



    @Test
    public void testRegister_EmailAlreadyExists() throws Exception {
        // Arrange - Register first user
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test1\",\"lastName\":\"User1\",\"age\":25,\"password\":\"password123\",\"email\":\"same@example.com\"}"));

        // Act & Assert - tries to register again with the same email
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test2\",\"lastName\":\"User2\",\"age\":30,\"password\":\"password456\",\"email\":\"same@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));
    }

    @Test
    public void testRegister_InvalidData() throws Exception {
        // Act & Assert - validation errors
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"lastName\":\"\",\"age\":15,\"password\":\"pass\",\"email\":\"invalid-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));  // Checks for errors
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Arrange - Register user first
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"lastName\":\"User\",\"age\":25,\"password\":\"password123\",\"email\":\"login@example.com\"}"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"login@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString()))); // Checker returned JWT token
    }

    @Test
    public void testLogin_wrongPassword() throws Exception{
        //Arrange
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"lastName\":\"User\",\"age\":25,\"password\":\"password123\",\"email\":\"test@example.com\"}"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"test@example.com\",\"password\":\"wrongPassword\"}"))
                .andExpect(status().isUnauthorized()) //status 401
                .andExpect(content().string("Invalid email or password"));
    }

    @Test
    public void testLogin_UserNotFound() throws Exception{
        //Arrange
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"lastName\":\"User\",\"age\":25,\"password\":\"password123\",\"email\":\"test@example.com\"}"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"login@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized()) //status 401
                .andExpect(content().string("Invalid email or password"));
    }
}
