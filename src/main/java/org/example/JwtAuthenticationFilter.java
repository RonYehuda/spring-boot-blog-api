package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JwtAuthenticationFilter - Security Filter for JWT Token Validation
 *
 * This filter intercepts EVERY HTTP request before it reaches any Controller
 * Purpose: Check if the request contains a valid JWT token and authenticate the user
 *
 * Extends OncePerRequestFilter to ensure this filter runs exactly once per request
 * (prevents multiple executions that could occur in complex filter chains)
 */

@Component // Spring manages this as a bean - automatically registered in filter chain
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired JwtUtil jwtUtil; // Utility for JWT operations (validate, extract data from tokens)

    /**
     * Core filter method - executed for every HTTP request
     *
     * @param request - Contains all information about the incoming HTTP request
     *                 (headers, URL, method, body, etc.)
     * @param response - Object used to send response back to client
     *                  (status codes, headers, content)
     * @param filterChain - Chain of filters and controllers that process the request
     *                     Used to pass request to next step if authentication succeeds
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Skip JWT validation for public endpoints
        if (requestPath.equals("/auth/login") || requestPath.equals("/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }
        // STEP 1: Extract Authorization header from HTTP request
        // Expected format: "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
        String authHeader = request.getHeader("Authorization");
        // STEP 2: Check if Authorization header exists and has correct format
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            // No token provided OR wrong format
            // This is OK for public endpoints like /auth/login, /auth/register
            // Pass request to next filter/controller without authentication
            filterChain.doFilter(request,response);
            return; // Exit filter - don't execute remaining code
        }

        // STEP 3: Extract actual JWT token (remove "Bearer " prefix)
        // "Bearer eyJhbGci..." becomes "eyJhbGci..."
        String token = authHeader.substring(7);  // Skip first 7 characters ("Bearer ")

        // STEP 4: Validate the JWT token using our JwtUtil
        if (!jwtUtil.validateToken(token)){
            // Token is invalid (expired, malformed, wrong signature, etc.)
            // Block the request and return error response

            response.setStatus(401);  // HTTP 401 Unauthorized
            response.getWriter().write("Invalid token"); // Error message in response body
            return; // STOP HERE - don't pass request to controller
        }
        // STEP 5: Token is valid - allow request to proceed
        // Pass request to next filter in chain, eventually reaching the Controller
        filterChain.doFilter(request,response);
    }
}
/*
 * HOW THIS FILTER WORKS IN THE REQUEST FLOW:
 *
 * 1. Client sends HTTP request with header: "Authorization: Bearer <jwt-token>"
 *
 * 2. Spring automatically calls this filter BEFORE any @Controller method
 *
 * 3. Filter checks token:
 *    - No token: Pass through (for public endpoints)
 *    - Invalid token: Return 401 error, STOP
 *    - Valid token: Pass to Controller
 *
 * 4. If passed through, request reaches @GetMapping/@PostMapping methods
 *
 * 5. Controller processes request and returns response
 *
 * SECURITY BENEFIT:
 * - Protects ALL endpoints automatically
 * - Controllers don't need to check authentication manually
 * - Centralized authentication logic
 * - Works with any JWT token we generate in AuthController
 */