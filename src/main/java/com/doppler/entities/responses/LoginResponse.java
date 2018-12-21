package com.doppler.entities.responses;

import com.doppler.entities.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The login response.
 */
@Getter
@Setter
@ToString(exclude = {"accessToken"})
public class LoginResponse {

  /**
   * The access token.
   */
  private String accessToken;

  /**
   * The user.
   */
  private User user;
}
