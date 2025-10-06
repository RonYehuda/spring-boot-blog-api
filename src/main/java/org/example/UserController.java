package org.example;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {
        logger.info("Creating new user: {} {}", user.getName(), user.getLastName());
        User savedUser = userRepository.save(user);
        String message = "Created user - ID: " + savedUser.getId() + ", Name: " + savedUser.getName();
        logger.info("User: {} {} created successfully with ID: {}", savedUser.getName(), savedUser.getLastName(), savedUser.getId());
        return ResponseEntity.status(201).body(message);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request){
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
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
       Optional<User> optionalUser = userRepository.findById(id);
       if (optionalUser.isPresent()) {
           return  ResponseEntity.ok(optionalUser.get());
       }
       return ResponseEntity.notFound().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @Valid @RequestBody User updateUser){
        logger.info("Updating user with ID: {}", id);
        Optional<User> optionalUser = userRepository.findById(id);
         if (optionalUser.isPresent()){
             User  user = optionalUser.get();
             user.setName(updateUser.getName());
             user.setAge(updateUser.getAge());
             user.setLastName(updateUser.getLastName());
             userRepository.save(user);
             logger.info("User {} {} with ID: {} updated successfully", user.getName(), user.getLastName(), id);
             String message = "User " + user.getName() + " " + user.getLastName() + " is updated.";
             return ResponseEntity.ok(message);
         }
        logger.warn("User with ID: {} not found for update", id);
         return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        logger.info("Deleting user with ID: {}", id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userRepository.delete(user);
            String message = "user: " + user.getName() + " " + user.getLastName() + " deleted";
            logger.info("User with ID: {} delete successfully", id);
            return ResponseEntity.ok(message);
        }
        logger.warn("User with ID: {} not found for deletion", id);
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users/age/{age}")
    public ResponseEntity<List<User>> findUserByAge(@PathVariable int age){
        List<User> users = userRepository.findByAge(age);
        if (users.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/age-above/{age}")
    public ResponseEntity<List<User>> findByAgeGreaterThan(@PathVariable int age){
        List<User> users = userRepository.findByAgeGreaterThan(age);
        if (users.isEmpty())return ResponseEntity.notFound().build();
        return ResponseEntity.ok(users);
    }

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
