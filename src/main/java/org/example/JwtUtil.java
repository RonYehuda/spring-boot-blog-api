package org.example;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

/**
 * Utility class for managing JWT tokens
 * Responsible for: creating, validating, and extracting information from JWT tokens
 */
@Component  // Tells Spring to manage this class as a bean
public class JwtUtil {
    // Secret key for encryption - must be at least 256 bits (32 characters)
    private String SECRET_KEY = "mySecretKey123456789012345678901234567890";

    /**
     * Generates a new JWT token for a user
     * @param email - the user's email
     * @return JWT token as String
     */
    public String generateToken(String email, Role role) {
        String token = Jwts.builder()  // Creates a JWT builder
                .setSubject(email)  // Stores the email as "subject" (token owner)
                .claim("role",role.toString())
                .setIssuedAt(new Date())  // Token creation time (now)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                // Expiration time: now + 10 hours (1000ms * 60s * 60min * 10h)
                .signWith(Keys
                        .hmacShaKeyFor(
                                SECRET_KEY.getBytes()))
                // Signs the token with the secret key:
                // 1. SECRET_KEY.getBytes() - converts String to bytes
                // 2. Keys.hmacShaKeyFor() - creates Key object from bytes
                // 3. signWith() - signs with the Key
                .compact();  // Builds the final token as String
        return token;
    }

    /**
     * Validates if a JWT token is valid
     * @param token - the token to validate
     * @return true if valid, false if not
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()  // Creates a parser for reading tokens
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    // Sets the key for signature verification
                    .build()  // Builds the parser - ready to use
                    .parseClaimsJws(token);
            // Parses and validates the token:
            // - Checks if signature is correct
            // - Checks if token hasn't expired
            // - Throws Exception if something is invalid
            return true;  // If we got here - no exception, token is valid
        } catch (Exception e) {
            return false;  // If exception was thrown - token is invalid
        }
    }

    /**
     * Extracts the email from a JWT token
     * @param token - the token to extract email from
     * @return the user's email
     */
    public String extractEmail(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()  // Creates parser
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))  // Sets key
                .build()  // Builds parser
                .parseClaimsJws(token);  // Parses token and returns object with data

        // claimsJws.getBody() - extracts the Claims (data) from token
        // claims.getSubject() - extracts the subject (the email we saved)
        String email = claimsJws.getBody().getSubject();
        return email;
    }

    public String extractRole(String token) {
        Jws<Claims> claimsJwts = Jwts.parserBuilder() // Creates parser
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())) // Sets key
                .build()  // Builds parser
                .parseClaimsJws(token); // Parses token and returns object with data

        // claimsJws.getBody() - extracts the Claims (data) from token
        // claims.get - extracts the role
        String role = claimsJwts.getBody().get("role",String.class);
        return role;
    }
}