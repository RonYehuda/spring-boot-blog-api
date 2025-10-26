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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Rollback

public class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testPublicEndpoint_Register_NoToken() throws Exception{
        // Arrange and Act
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test1\",\"lastName\":\"User1\",\"age\":25,\"password\":\"password123\",\"email\":\"same@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void  testProtectedEndpoint_NoToken() throws Exception{
        // Act & Assert
        mockMvc.perform(get("/users"))  //← Without contentType and content because it is a GET request
                .andExpect(status().isUnauthorized());  // ← 401
    }

    @Test
    public void testProtectedEndpoint_WithValidToken() throws Exception{
        // Arrange
        String token = jwtUtil.generateToken("test@example.com" , Role.ADMIN);

        // Act & Assert
        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + token))// ← This is where we send the header!
                .andExpect(status().isOk());

    }

    @Test
    public void testProtectedEndpoint_WithInvalidToken() throws Exception{
        //Arrange
        String token = "fake Token 1234";

        //Act Assert
        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());

    }

}
