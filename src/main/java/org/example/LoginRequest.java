package org.example;
/**
 * LoginRequest - Data Transfer Object (DTO)
 * Simple class that holds login credentials sent from client to server
 * Used specifically for the /auth/login endpoint
 * Contains only data - no business logic
 */
public class LoginRequest {

    // Private fields to store login credentials
    private String email;    // User's email address
    private String password; // User's plain text password (will be verified against hashed version)

    /**
     * Default no-argument constructor
     * REQUIRED by Spring Framework for JSON deserialization
     * Spring uses this to create empty object, then calls setters to populate data
     */
    public LoginRequest() {}

    // Getter and Setter methods
    // Spring uses these during JSON to Object conversion process

    /**
     * Gets the email address
     * @return email as String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address
     * Called by Spring when converting JSON {"email": "value"} to object
     * @param email - the email from JSON request
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password
     * @return password as String (plain text)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password
     * Called by Spring when converting JSON {"password": "value"} to object
     * @param password - the plain text password from JSON request
     */
    public void setPassword(String password) {
        this.password = password;
    }
}

/*
 * HOW THIS CLASS WORKS WITH SPRING:
 *
 * 1. Client sends HTTP POST with JSON body:
 *    {
 *      "email": "test@example.com",
 *      "password": "mypassword123"
 *    }
 *
 * 2. Spring Framework automatically:
 *    - Creates new LoginRequest() using default constructor
 *    - Calls setEmail("test@example.com")
 *    - Calls setPassword("mypassword123")
 *    - Passes populated object to AuthController.login() method
 *
 * 3. AuthController receives ready-to-use LoginRequest object with data
 *
 * This process is called "JSON Deserialization" or "Data Binding"
 */