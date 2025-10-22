package org.example;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {
    @Test
    public void testGenerateToken(){
        // 1. Arrange
        JwtUtil jwtUtil = new JwtUtil();
        String email = "test@example.com";
        Role role = Role.USER;

        // 2. Act
        String token = jwtUtil.generateToken(email, role);

        // 3. Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(email,jwtUtil.extractEmail(token));
        assertEquals(role.toString(),jwtUtil.extractRole(token));
        assertTrue(jwtUtil.validateToken(token));
        }

    @Test
    public void testValidateToken_TamperedToken(){
        // Arrange
        JwtUtil jwtUtil = new JwtUtil();
        String validToken = jwtUtil.generateToken("test@example.com", Role.USER);

        // Act create fake token by change the signature
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";

        // Assert
        assertFalse(jwtUtil.validateToken(tamperedToken));
    }

    @Test
    public void testValidateToken_NullToken(){
        // Arrange
        JwtUtil jwtUtil = new JwtUtil();

        // Act & Assert
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    public void testValidateToken_EmptyToken(){
        // Arrange
        JwtUtil jwtUtil = new JwtUtil();

        // Act & Assert
        assertFalse(jwtUtil.validateToken(""));
    }
    @Test
    public void testExtractEmail(){
        // 1. Arrange
        String email = "test@example.com";
        Role role = Role.USER;
        JwtUtil jwtUtil = new JwtUtil();

        // 2. Act
        String token = jwtUtil.generateToken(email, role);
        String extractedEmail  = jwtUtil.extractEmail(token);
        // 3. Assert
        assertEquals(email, extractedEmail );
    }
    @Test
    public void testExtractEmail_InvalidToken(){
        // Arrange
        JwtUtil jwtUtil = new JwtUtil();
        String invalidToken = "invalid.token.here";

        // Act & Assert -> exception
        assertThrows(Exception.class, () -> {
            jwtUtil.extractEmail(invalidToken);
        });
    }

    @Test
    public void testExtractRole(){
        // 1. Arrange
        JwtUtil jwtUtil = new JwtUtil();
        String email = "test@example.com";
        Role role = Role.ADMIN;

        // 2. Act
        String token = jwtUtil.generateToken(email, role);
        String extractedRole = jwtUtil.extractRole(token);

        // 3. Assert
        assertEquals(role.toString(), extractedRole);
    }

    @Test
    public void testExtractRole_InvalidToken() {
        // Arrange
        JwtUtil jwtUtil = new JwtUtil();
        String invalidToken = "invalid.token.here";

        // Act & Assert ->exception
        assertThrows(Exception.class, () -> {
            jwtUtil.extractRole(invalidToken);
        });
    }

}
