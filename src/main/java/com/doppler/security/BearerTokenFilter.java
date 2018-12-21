package com.doppler.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.doppler.entities.User;
import com.doppler.services.SecurityService;

/**
 * The filter to process bearer authentication token.
 */
public class BearerTokenFilter extends OncePerRequestFilter {

  /**
   * The bearer prefix.
   */
  private static final String BEARER_PREFIX = "Bearer ";

  /**
   * The token utils.
   */
  @Autowired
  private TokenUtils tokenUtils;

  /**
   * The security service.
   */
  @Autowired
  private SecurityService securityService;

  /**
   * The machine-2-machine token.
   */
  @Value("${token.m2m}")
  private String m2mToken;

  /**
   * Process the request.
   * 
   * @param request the HTTP servlet request
   * @param response the HTTP servlet response
   * @param chain the filter chain
   * @throws IOException if an I/O error occurs during the processing of the request
   * @throws ServletException if the processing fails for any other reason
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
      bearerToken = bearerToken.substring(BEARER_PREFIX.length());
    }

    if (!StringUtils.isEmpty(bearerToken)) {
      UsernamePasswordAuthenticationToken authentication = null;

      if (m2mToken.equals(bearerToken)) {
        // Machine token
        authentication = new UsernamePasswordAuthenticationToken("m2m", null,
            Arrays.asList(new SimpleGrantedAuthority("ROLE_M2M")));
      } else {
        // Decode the user token
        UUID userId = tokenUtils.decodeJwtToken(bearerToken);
        User user = securityService.get(userId);

        // Set the user info to security context.
        authentication = new UsernamePasswordAuthenticationToken(user, null,
            Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
      }

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    chain.doFilter(request, response);
  }
}
