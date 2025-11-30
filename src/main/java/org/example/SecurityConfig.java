package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
/**
 * SecurityConfig - Spring Security Configuration Class
 *
 * This class configures the security layer of the application:
 * - Which endpoints require authentication (protected) and which don't (public)
 * - Integration of JWT authentication filter into the security chain
 * - Password encoding configuration
 * - Exception handling for authentication failures
 */
@Configuration  // Tells Spring this is a configuration class
@EnableWebSecurity  // Enables Spring Security for the application
@EnableMethodSecurity  // Enables @PreAuthorize on methods to check user roles before method execution

public class SecurityConfig {

    // Inject our custom JWT filter that validates tokens
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Security Filter Chain Configuration
     *
     * This method defines the security rules for HTTP requests:
     * - Which endpoints are public (no authentication needed)
     * - Which endpoints are protected (authentication required)
     * - Which filters should process requests and in what order
     * - How to handle authentication failures
     *
     * @param httpSecurity - Object for configuring HTTP security
     * @return SecurityFilterChain - The configured security chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Disable CSRF protection (not needed for stateless JWT authentication)
                // CSRF is only relevant for session-based authentication with cookies
                .csrf(csrf -> csrf.disable())

                // Configure session management for stateless JWT authentication
                // STATELESS means no session will be created or used by Spring Security
                // Each request must contain a valid JWT token
                // This is essential for scalable microservices architecture
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure authorization rules for HTTP requests
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints: /auth/login and /auth/register
                        // Anyone can access these without authentication
                        // These are needed for user registration and obtaining JWT tokens
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Public GET endpoints for posts - anyone can view posts
                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()           // All GET /posts/** endpoints
                        .requestMatchers(HttpMethod.GET, "/users/*/posts").permitAll()      // GET /users/{id}/posts endpoint

                        // All other endpoints require authentication
                        // User must provide valid JWT token in Authorization header
                        .anyRequest().authenticated())

                // Configure exception handling for authentication failures
                // When authentication is missing or invalid, return 401 Unauthorized
                // instead of default 403 Forbidden
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);  // HTTP 401 Unauthorized
                            response.getWriter().write("Unauthorized");
                        }))

                // Add our JWT filter BEFORE the default Spring Security filter
                // This ensures JWT validation happens first in the filter chain
                // Order matters: JWT check → Spring Security authentication → Authorization
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    /**
     * Password Encoder Bean
     *
     * Defines how passwords are encrypted/hashed in the application
     * BCrypt is a strong one-way hashing algorithm that:
     * - Automatically generates salt for each password
     * - Is computationally expensive to crack
     * - Is the industry standard for password hashing
     *
     * @return PasswordEncoder - BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
