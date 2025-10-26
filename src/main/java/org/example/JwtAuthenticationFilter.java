package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JwtAuthenticationFilter - Security Filter for JWT Token Validation
 *
 * This filter intercepts EVERY HTTP request before it reaches any Controller
 * Purpose: Check if the request contains a valid JWT token and authenticate the user
 *
 * Extends OncePerRequestFilter to ensure this filter runs exactly once per request
 * (prevents multiple executions that could occur in complex filter chains)
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;

    /**
     * Core filter method - executed for every HTTP request
     *
     * @param request - Contains all information about the incoming HTTP request
     * @param response - Object used to send response back to client
     * @param filterChain - Chain of filters and controllers that process the request
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
        String authHeader = request.getHeader("Authorization");

        // STEP 2: Check if Authorization header exists and has correct format
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        // STEP 3: Extract actual JWT token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        // STEP 4: Validate the JWT token
        if (!jwtUtil.validateToken(token)){
            response.setStatus(401);
            response.getWriter().write("Invalid token");
            return;
        }

        // STEP 5: Token is valid - Extract user information
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        // STEP 6: Create Authentication object
        // This tells Spring Security: "This user is authenticated!"
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,           // principal (who is the user)
                        null,            // credentials (no password needed here)
                        new ArrayList<>() // authorities (roles) - empty for now
                );

        // STEP 7: Set authentication in SecurityContext
        // Spring Security will now recognize this user as authenticated
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // STEP 8: Pass request to next filter/controller
        filterChain.doFilter(request,response);
    }
}