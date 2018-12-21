package com.doppler.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import com.doppler.entities.User;

/**
 * This class provides utility methods related to security.
 */
public abstract class SecurityUtils {

  /**
   * Private constructor.
   */
  private SecurityUtils() {}

  /**
   * Get current logged in user.
   * 
   * @return the current logged in user
   * @throws AccessDeniedException if the user principal is not in the context
   */
  public static User getCurrentUser() {
    if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User) {
      return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    throw new AccessDeniedException("You are not authorized to access this resource");
  }
}
