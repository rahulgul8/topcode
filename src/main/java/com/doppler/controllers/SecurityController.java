package com.doppler.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.doppler.entities.requests.LoginRequest;
import com.doppler.entities.responses.LoginResponse;
import com.doppler.services.SecurityService;

/**
 * The controller defines security related endpoints.
 */
@RestController
public class SecurityController {

  /**
   * The security service.
   */
  @Autowired
  private SecurityService securityService;

  /**
   * Authenticate the login request.
   * 
   * @param request the login request
   * @return the login response
   */
  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return securityService.login(request);
  }
}
