package com.doppler.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doppler.entities.User;
import com.doppler.entities.requests.LoginRequest;
import com.doppler.entities.responses.LoginResponse;
import com.doppler.repositories.UserRepository;
import com.doppler.security.TokenUtils;

/**
 * The service provides security related operations.
 */
@Service
@Transactional
public class SecurityService {

  /**
   * The user repository.
   */
  @Autowired
  private UserRepository userRepository;
  
  
  @Autowired
  LoginRestService loginService;

  /**
   * The token utils.
   */
  @Autowired
  private TokenUtils tokenUtils;

  /**
   * Login a user.
   * 
   * @param request the login request
   * @return the login response
   */
  public LoginResponse login(LoginRequest request) {
    // Authenticate
    User user = authenticateWithSSO(request);

    // Check if registered or not
    User dbUser = userRepository.findByEmailIgnoreCase(request.getEmail());
    if (dbUser != null) {
      user.setId(dbUser.getId());
      user.setPoints(dbUser.getPoints());
      user.setIsNew(dbUser.getIsNew());
      user.setNotifiedByNewEvents(dbUser.isNotifiedByNewEvents());
    }
    doBackendLogin(user);
    // Save
    user = userRepository.save(user);

    // Generate token
    String accessToken = tokenUtils.createJwtToken(user.getId());

    // Build the response
    LoginResponse response = new LoginResponse();
    response.setAccessToken(accessToken);
    response.setUser(user);

    return response;
  }

  /**
   * Get user by id.
   * 
   * @param id the user UUID
   * @return the user
   */
  public User get(UUID id) {
    return userRepository.findById(id).get();
  }

  /**
   * Authenticate with SSO.
   * 
   * @param request the request
   * @return the user
   * @throws BadCredentialsException if the email or password is incorrect
   */
  private User authenticateWithSSO(LoginRequest request) throws BadCredentialsException {
    // FIXME this is a mock

    User user = new User();
    user.setEmail(request.getEmail());
    user.setDivision("division");
    user.setFullName("full name");
    user.setLocation("location");
    user.setPhoneNumber("12345678");
    user.setPhotoUrl("http://example.com/photo.jpg");
    user.setPosition("position");
    user.setIsNew(true);

    return user;
  }
  
  
  private User doBackendLogin(User user) {
	  user.setBackendToken(loginService.userLogin());
	  return user;
  }
}
