package org.example;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
/**
 * AuthController - Handles user authentication operations
 * Provides endpoints for user registration and login
 * Now integrated with JWT token generation for login
 */
@RestController // Makes this class a REST API controller
public class AuthController {
    private static final Logger logger= LoggerFactory.getLogger(AuthController.class);

    // Dependency injection - Spring automatically provides these objects
    @Autowired
    private UserRepository userRepository; // For database operations on users
    @Autowired
    private PasswordEncoder passwordEncoder; // For hashing and verifying passwords
    @Autowired
    private JwtUtil jwtUtil; // NEW: For generating and validating JWT tokens

    /**
     * User Registration endpoint
     * Creates new user account with encrypted password
     * @param user - User data from JSON request body
     * @return ResponseEntity with success/error message
     */
    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user){
        logger.info("Creating new user: {} {}", user.getName(), user.getLastName());
        // Check if email already exists in database
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        // If email exists, return error (emails must be unique)
        if (optionalUser.isPresent())return ResponseEntity.badRequest().body("Email already exists");

        // Hash the password before saving (NEVER store plain text passwords)
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Save user to database
        userRepository.save(user);
        logger.info("User: {} {} created successfully with ID: {}"
                , user.getName(), user.getLastName(), user.getId());
        return  ResponseEntity.ok("User registered successfully");
    }

    /**
     * User Login endpoint - UPDATED FOR JWT
     * Authenticates user and returns JWT token instead of simple message
     * @param loginRequest - Contains email and password
     * @return ResponseEntity with JWT token or error message
     */
    @PostMapping("/auth/login")
    public ResponseEntity<String>login(@RequestBody LoginRequest loginRequest){
        logger.info("User with email:{} ,try to login.",loginRequest.getEmail());
        // Search for user by email in database
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        // If user exists in database
        if (optionalUser.isPresent()){
            User userToCheck = optionalUser.get();  // Extract user from Optional

            // Verify password: compare plain text with hashed password
            // passwordEncoder.matches(plainText, hashedPassword) returns true/false
            if (passwordEncoder.matches(loginRequest.getPassword(), userToCheck.getPassword())){
                logger.info("User login successfully.");
                // NEW JWT FUNCTIONALITY:
                // Instead of returning "Login successful" message,
                // generate JWT token containing user's email and return it
                String jwtToken = jwtUtil.generateToken(loginRequest.getEmail(), userToCheck.getRole());
                return ResponseEntity.ok(jwtToken);  // Return the actual JWT token
            }
            // Password doesn't match - return same error as "user not found" (security)
            return ResponseEntity.status(401).body("Invalid email or password");
        }
        // User not found - don't reveal if email exists (security best practice)
        logger.info("User notFound, can not login.");
        return ResponseEntity.status(401).body("Invalid email or password");
    }
}
