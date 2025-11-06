package org.example;


import org.springframework.security.core.Authentication;

/**
 * AuthUtil - Utility class for authorization checks
 *
 * Provides reusable static methods for checking user permissions
 * without needing to instantiate the class or use @Autowired
 */
public class AuthUtil {
    /**
     * Check if the current user is authorized to access a resource
     *
     * Authorization rules:
     * - ADMIN can access any resource (returns true)
     * - USER can only access their own resources (email must match)
     *
     * @param authentication - Current user's authentication info from JWT token
     * @param resourceOwnerEmail - Email of the resource owner (user/post)
     * @return true if authorized (ADMIN or owner), false otherwise
     *
     * Example usage:
     * if (!AuthUtil.isAdminOrOwner(authentication, user.getEmail())) {
     *     return ResponseEntity.status(403).body("Access denied");
     * }
     */

    public static boolean isAdminOrOwner(Authentication authentication, String resourceOwnerEmail){
        // Extract current user's email from JWT token
        String currentUserEmail = authentication.getName();

        // Extract current user's role from JWT token
        String currentUserRole = authentication.getAuthorities().iterator().next().getAuthority();

        // Check if user is ADMIN
        boolean isAdmin = "ROLE_ADMIN".equals(currentUserRole);

        // Check if user is the owner (email matches)
        boolean isOwner = resourceOwnerEmail.equals(currentUserEmail);

        // Return true if ADMIN OR owner
        return isAdmin || isOwner;
    }
}
