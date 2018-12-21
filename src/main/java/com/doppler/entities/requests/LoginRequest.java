package com.doppler.entities.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The login request.
 */
@Getter
@Setter
@ToString(exclude = {"password"})
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  /**
   * The email.
   */
  @NotBlank
  @Email
  private String email;

  /**
   * The password.
   */
  @NotBlank
  private String password;
}
