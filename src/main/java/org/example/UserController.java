package org.example;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;


    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can create users
    @PostMapping("/users")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {
        logger.info("Creating new user: {} {}", user.getName(), user.getLastName());
        User savedUser = userRepository.save(user);
        String message = "Created user - ID: " + savedUser.getId() + ", Name: " + savedUser.getName();
        logger.info("User: {} {} created successfully with ID: {}", savedUser.getName(), savedUser.getLastName(), savedUser.getId());
        return ResponseEntity.status(201).body(message);
    }

    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can view all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request){
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
       /* ===== OLD CODE - BEFORE ROLE-BASED AUTHORIZATION =====
       String authHeader  = request.getHeader("Authorization"); //Extract the token
        if (authHeader == null || !authHeader.startsWith("Bearer ")){ // Check if header exists and has correct format
            return ResponseEntity.status(401).body(null);
        }
        String token = authHeader.substring(7); // remove the "Bearer "
        if ("ADMIN".equals(jwtUtil.extractRole(token))){
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        }
        return ResponseEntity.status(403).body(null); // Forbidden(not an ADMIN)
        ======================================================*/

    }

    @PreAuthorize("isAuthenticated()") // Any authenticated user can view a user by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
       Optional<User> optionalUser = userRepository.findById(id);
       if (optionalUser.isPresent()) {
           return  ResponseEntity.ok(optionalUser.get());
       }
       return ResponseEntity.notFound().build();
    }

    @PreAuthorize("isAuthenticated()")  // Must be logged in
    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id,
                                             @Valid @RequestBody User updateUser,
                                             Authentication authentication){
        logger.info("Updating user with ID: {}", id);

        // Find user to update
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.warn("User with ID: {} not found for update", id);
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // check if the user is ADMIN or own this profile
        if(!AuthUtil.isAdminOrOwner(authentication, user.getEmail())){
            logger.warn("User {} attempted to update user ID: {} without authorization",authentication.getName(), id);
            return ResponseEntity.status(403).body("You can only update your own profile");
        }

        // Update user
        user.setName(updateUser.getName());
        user.setAge(updateUser.getAge());
        user.setLastName(updateUser.getLastName());
        userRepository.save(user);

        logger.info("User {} {} with ID: {} updated successfully", user.getName(), user.getLastName(), id);
        String message = "User " + user.getName() + " " + user.getLastName() + " is updated.";
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("isAuthenticated()")  // Must be logged in
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, Authentication authentication){
        logger.info("Deleting user with ID: {}", id);

        // Find user to delete
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.warn("User with ID: {} not found for deletion", id);
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        if(!AuthUtil.isAdminOrOwner(authentication, user.getEmail())){
            logger.warn("User {} attempted to delete user ID: {} without authorization",authentication.getName(), id);
            return ResponseEntity.status(403).body("You can only delete your own profile");
        }

        // Delete user
        userRepository.delete(user);
        String message = "user: " + user.getName() + " " + user.getLastName() + " deleted";
        logger.info("User with ID: {} deleted successfully", id);
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("isAuthenticated()")  // Any authenticated user can search by age
    @GetMapping("/users/age/{age}")
    public ResponseEntity<List<User>> findUserByAge(@PathVariable int age){
        List<User> users = userRepository.findByAge(age);
        if (users.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("isAuthenticated()")  // Any authenticated user can search by age range
    @GetMapping("/users/age-above/{age}")
    public ResponseEntity<List<User>> findByAgeGreaterThan(@PathVariable int age){
        List<User> users = userRepository.findByAgeGreaterThan(age);
        if (users.isEmpty())return ResponseEntity.notFound().build();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")  // Only ADMIN can search by email (sensitive data)
    @GetMapping("/users/email/{email}")
    public ResponseEntity<User> findUserByEmail(@PathVariable String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
}
