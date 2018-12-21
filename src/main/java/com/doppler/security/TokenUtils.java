package com.doppler.security;

import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * This utility class contains helper methods used to generate and decode JWT tokens.
 */
@Component
public class TokenUtils {

  /**
   * The claim key.
   */
  private static final String CLAIM_KEY = "sub";

  /**
   * The secret to sign the tokens.
   */
  @Value("${jwt.secret}")
  private String secret;

  /**
   * The token expiration in seconds.
   */
  @Value("${jwt.expiration-in-seconds}")
  private long expirationInSeconds;

  /**
   * Creates JWT token for the specified token payload.
   * 
   * @param tokenPayload the token payload
   * @return the JWT token
   */
  public String createJwtToken(UUID tokenPayload) {
    final Date expiration = new Date(System.currentTimeMillis() + expirationInSeconds * 1000);

    return Jwts.builder().claim(CLAIM_KEY, tokenPayload.toString()).setExpiration(expiration)
        .signWith(SignatureAlgorithm.HS512, secret).compact();
  }

  /**
   * Decodes the JWT token.
   * 
   * @param jwtToken the JWT token
   * @return the user UUID
   * @throws BadCredentialsException if the JWT token is invalid
   */
  public UUID decodeJwtToken(String jwtToken) {
    Claims claims;

    try {
      claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtToken).getBody();
    } catch (Exception ex) {
      throw new BadCredentialsException(ex.getMessage(), ex);
    }

    String payload = (String) claims.get(CLAIM_KEY);
    try {
      return UUID.fromString(payload);
    } catch (IllegalArgumentException ex) {
      throw new BadCredentialsException("Invalid JWT token", ex);
    }
  }
}
